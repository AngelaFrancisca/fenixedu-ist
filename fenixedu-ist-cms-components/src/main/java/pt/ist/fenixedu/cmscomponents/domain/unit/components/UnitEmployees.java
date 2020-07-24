/**
 * Copyright © 2013 Instituto Superior Técnico
 *
 * This file is part of FenixEdu IST CMS Components.
 *
 * FenixEdu IST CMS Components is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu IST CMS Components is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu IST CMS Components.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixedu.cmscomponents.domain.unit.components;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import pt.ist.fenixedu.contracts.domain.Employee;
import pt.ist.fenixedu.contracts.domain.accessControl.ActiveEmployees;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@ComponentType(name = "Unit Employees", description = "Unit employees that are not teachers")
public class UnitEmployees extends UnitSiteComponent {

    private Predicate<Employee> isEmployee = employee -> new ActiveEmployees().isMember(employee.getPerson().getUser());
    private static Predicate<Employee> hasWorkingPlace = employee -> employee.getCurrentWorkingPlace() != null;
    private static Supplier<TreeMap<Unit, TreeSet<Employee>>> mapFactory = () -> Maps.newTreeMap(Unit.COMPARATOR_BY_NAME_AND_ID
            .reversed());
    private static Supplier<TreeSet<Employee>> sortedEmployeesFactory = () -> Sets.newTreeSet((e1, e2) -> e1.getPerson()
            .compareTo(e2.getPerson()));

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        Unit unit = unit(page);
        SortedMap<Unit, TreeSet<Employee>> employeesMap = nonTeacherEmployeesByWorkingPlace(unit(page));

        globalContext.put("employees", activeNonTeacherEmployeesStream(unit(page)).map(Employee::getPerson).collect(toList()));
        globalContext.put("hasEmployeesNoArea", activeNonTeacherEmployeesStream(unit).filter(hasWorkingPlace.negate()).findAny()
                .isPresent());
        globalContext.put("employeesNoArea", activeNonTeacherEmployeesStream(unit).filter(hasWorkingPlace.negate()));
        globalContext.put("employeesByArea", employeesMap);
    }

    private SortedMap<Unit, TreeSet<Employee>> nonTeacherEmployeesByWorkingPlace(Unit unit) {
        return activeNonTeacherEmployeesStream(unit).filter(hasWorkingPlace).collect(
                groupingBy(Employee::getCurrentWorkingPlace, mapFactory, toCollection(sortedEmployeesFactory)));
    }

    private Stream<Employee> activeNonTeacherEmployeesStream(Unit unit) {
		return Employee.getAllCurrentActiveWorkingEmployees(unit).stream()
				.filter(Objects::nonNull).filter(isEmployee);
    }

}
