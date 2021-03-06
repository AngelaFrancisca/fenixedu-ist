/*
 * Copyright © 2013 Instituto Superior Técnico
 *
 * This file is part of FenixEdu IST Integration.
 *
 * FenixEdu IST Integration is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu IST Integration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu IST Integration.  If not, see <http://www.gnu.org/licenses/>.
 */
var teacherApp = angular.module('pagesApp', ['bennuToolkit', 'angularFileUpload']);

teacherApp.directive('tooltip', function(){
    return {
        restrict: 'A',
        link: function(scope, element, attrs){
            window.tooltip.apply($(element));
        }
    };
});

teacherApp.controller('PagesCtrl', ['$scope', '$http', '$upload', function ($scope, $http, $upload) {

    $scope.selectedFile = undefined;

    $scope.context = context || window.location.pathname;

    $scope.groups = [];

    $scope.handleError = function (data) {
        $scope.error = data;
        $scope.saving = false;
    };

    $scope.selectFile = function(file) {
        $scope.selectedFile = file;
    };

    $scope.onImageAdded = function (files, callback, el) {


        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            $scope.upload = $upload.upload({
                url: context + '/' + $scope.selected.key + '/addFile.json',
                method: 'POST',
                file: file,
                fileName: file.name
            }).progress(function (evt) {
                console.log('progress: ' + parseInt(100.0 * evt.loaded / evt.total) + '% file :' + evt.config.file.name);
            }).success(function (data, status, headers, config) {
                var urls = [];
                urls.push(data.downloadUrl);
                callback(urls);
            });
        }

    };

    var add = function (item, parent) {
        var isFolder = item.children && item.children.length > 0;
        item.node = parent.addChildren({ key: item.key, title: i18n(item.title), folder: isFolder, item: item });
        if(!item.body || Object.keys(item.body).length == 0) {
            item.body = emptyLocalizedString();
        }
        if (isFolder) {
            for (var i = 0; i < item.children.length; ++i) {
                item.children[i] && add(item.children[i], item.node);
            }
        }
        return item.node;
    };

    $scope.createChild = function () {
        var newItem = { title: initialTitle(), body: emptyLocalizedString(), position: 0 };
        add(newItem, $scope.selected.node).setActive(true);
        $('#pageTabLink').tab('show');
    };

    $scope.deleteSelected = function () {
        var node = $scope.selected.node;
        var parent = $scope.selected.node.parent;
        if ($scope.selected.key) {
            $http.delete($scope.context + "/" + $scope.selected.key)
                .success(function(){ node.remove(); })
                .error($scope.handleError);
        } else {
            node.remove();
        }
        $scope.selected = parent.data.item;
        parent.setActive(true);
    };

    $scope.deleteSelectedFile = function() {
        if($scope.selectedFile) {
            $http.delete($scope.context + "/attachment/" + $scope.selected.key + "/" + $scope.selectedFile.externalId)
                .success(function(data) {
                    $scope.selected.files = data;
                })
                .error($scope.handleError);
        }
    };

    $scope.saveSelected = function () {
        var data = {
            title: $scope.selected.title,
            body: $scope.selected.body,
            menuItemId: $scope.selected.key,
            menuItemParentId:  $scope.selected.node.parent.data.item.key,
            canViewGroupIndex: $scope.selected.canViewGroupIndex,
            visible: $scope.selected.visible
        };
        $scope.selected.saving = true;
        $scope.data = data;
        var promise;
        if ($scope.selected.key) {
            promise = $http.put($scope.context, data);
        } else {
            promise = $http.post($scope.context, JSON.stringify(data));
        }
        $scope.error = null;
        $scope.saving = true;
        promise.success(function (newItem) {
            newItem.loaded = true;
            if(Object.keys(newItem.body).length == 0) {
                newItem.body = emptyLocalizedString();
            }
            var node = $scope.selected.node;
            node.parent.folder = node.parent.children && node.parent.children.length > 0;
            $scope.selected = newItem;
            newItem.node = node;
            node.setTitle(i18n(newItem.title));
            node.data.item = newItem;
        }).error($scope.handleError);
    };

    $scope.move = function(item, insertAfter) {
        $http.put($scope.context + '/move',
            { menuItemId: item.key, parent: item.menuItemParentId, insertAfter: insertAfter ? insertAfter.key : null }).success(function (data) {
                item.position = data.position;
            });
    }

    $http.get($scope.context + "/data").then(function (response) {
        var data = response.data;
        $scope.loaded = true;
        $scope.error = false;

        $("#tree").fancytree({ source: [], extensions: ["dnd"],
            dnd: {
                preventVoidMoves: true, // Prevent dropping nodes 'before self', etc.
                preventRecursiveMoves: true, // Prevent dropping nodes on own descendants
                autoExpandMS: 400,
                dragStart: function (node, data) {
                    $scope.selected = node.data.item;
                    return true;
                },
                dragEnter: function (node, data) {  return true; },
                dragDrop: function (node, data) {
                    if(!node.data.item.root || data.hitMode !== "before") {
                        var insertAfter = null;
                        if (data.hitMode === "before") {
                            //if placed before -> take the position of that item
                            var other = node.parent.children[node.parent.children.indexOf(node) - 1];
                            if (other) {
                                insertAfter = other.data.item;
                            }
                            $scope.selected.menuItemParentId = node.parent.key;
                        }
                        else if (data.hitMode === "after") {
                            //if placed after -> take the position after that item
                            insertAfter = node.data.item;
                            $scope.selected.menuItemParentId = node.parent.data.item.key;
                        } else if (data.hitMode === "over") {
                            //over a given item -> take the position of first child of that item
                            $scope.selected.menuItemParentId = node.data.item.key;
                        }
                        data.otherNode.moveTo(node, data.hitMode);
                        $scope.move($scope.selected, insertAfter);
                    }
                }
            }
        });

        var tree = $("#tree").fancytree("getTree");

        $("#tree").bind("fancytreeactivate", function (event, data) {
            var item = data.node.data.item;
            if(item.key == undefined) {
                item.loaded = true;
            } else if(!item.loaded && item.key !== 'null') {
                $http.get($scope.context + '/data/' + item.key).success(function (data) {
                    item.body = data;
                    item.loaded = true;
                });
            }
            $scope.selected = item;
            $scope.error = null;
            if (!$scope.$$phase) {
                $scope.$apply();
            }
        });

        $scope.groups = data.groups;
        add(data.root, tree.rootNode);
        tree.rootNode.children[0].setActive(true);
        tree.rootNode.children[0].setExpanded(true);
        $("#tree ul").focus();

        if(window.location.hash) {
            tree.activateKey(window.location.hash.split('#')[1]);
            $('#pageFilesLink').tab('show');
        }
    }, function(response){
        $scope.loaded = true;
        $scope.error = response.status;
        $scope.errorMsg = response.data;
    });

    $scope.updateFile = function(file, newPosition) {
        var msg = { menuItemId: $scope.selected.key, fileId: file.externalId, position: newPosition, name: file.name, group: file.group, visible: file.visible };
        $scope.error = null;
        $scope.saving = true;
        $http.put($scope.context + "/attachment", msg)
            .success(function (updatedFiles) { $scope.selected.files = updatedFiles; })
            .error($scope.handleError);
    };

    function emptyLocalizedString() {
        var mlsBody = {};
        mlsBody[Bennu.locale.tag] = '';
        return mlsBody;
    }

    function initialTitle() {
        var mlsBody = {};
        mlsBody[Bennu.locale.tag] = 'New Entry';
        return mlsBody;
    }

    $scope.$watch('files', function() {
        for (var i = 0; i < $scope.files.length; i++) {
            console.log(JSON.stringify($scope.files[i]));
            var file = $scope.files[i];
            $scope.upload = $upload.upload({
                url: $scope.context + '/attachment/' + $scope.selected.key,
                method: 'POST',
                file: file,
                fileName: file.name
            }).progress(function(evt) {
                console.log('progress: ' + parseInt(100.0 * evt.loaded / evt.total) + '% file :'+ evt.config.file.name);
            }).success(function(data, status, headers, config) {
                $scope.selected.files = data;
            });
        }
    });

    $scope.addFileLink = function (file) {
        document.execCommand('createlink', false, file.downloadUrl);
        setTimeout(function () {
            $(".bennu-html-editor-editor").trigger("change");
        }, 100);
    };

    setTimeout(function () {
        $('.bennu-html-editor-toolbar.btn-toolbar').append($('#fileLinkBtnToolbar').detach());
    }, 200);

}]);

function i18n(input) {
    return Bennu.localizedString.getContent(input, Bennu.locale);
}