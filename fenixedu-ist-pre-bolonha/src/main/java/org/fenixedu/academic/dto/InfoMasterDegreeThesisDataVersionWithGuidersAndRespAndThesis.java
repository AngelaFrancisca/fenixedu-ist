/**
 * Copyright © 2013 Instituto Superior Técnico
 *
 * This file is part of FenixEdu IST Pre Bolonha.
 *
 * FenixEdu IST Pre Bolonha is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu IST Pre Bolonha is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu IST Pre Bolonha.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.dto;

import org.fenixedu.academic.domain.MasterDegreeThesisDataVersion;

/**
 * 
 * @author <a href="mailto:sana@ist.utl.pt">Shezad Anavarali </a>
 * 
 */
public class InfoMasterDegreeThesisDataVersionWithGuidersAndRespAndThesis extends
        InfoMasterDegreeThesisDataVersionWithGuidersAndResp {

    @Override
    public void copyFromDomain(MasterDegreeThesisDataVersion masterDegreeThesisDataVersion) {
        super.copyFromDomain(masterDegreeThesisDataVersion);
        if (masterDegreeThesisDataVersion != null) {
            setInfoMasterDegreeThesis(InfoMasterDegreeThesis.newInfoFromDomain(masterDegreeThesisDataVersion
                    .getMasterDegreeThesis()));

        }
    }

    public static InfoMasterDegreeThesisDataVersion newInfoFromDomain(MasterDegreeThesisDataVersion masterDegreeThesisDataVersion) {
        InfoMasterDegreeThesisDataVersionWithGuidersAndRespAndThesis infoMasterDegreeThesisDataVersionWithGuidersAndRespAndThesis =
                null;
        if (masterDegreeThesisDataVersion != null) {
            infoMasterDegreeThesisDataVersionWithGuidersAndRespAndThesis =
                    new InfoMasterDegreeThesisDataVersionWithGuidersAndRespAndThesis();
            infoMasterDegreeThesisDataVersionWithGuidersAndRespAndThesis.copyFromDomain(masterDegreeThesisDataVersion);
        }
        return infoMasterDegreeThesisDataVersionWithGuidersAndRespAndThesis;
    }
}