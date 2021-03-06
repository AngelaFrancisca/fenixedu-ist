/**
 * Copyright © 2013 Instituto Superior Técnico
 *
 * This file is part of FenixEdu IST Tutorship.
 *
 * FenixEdu IST Tutorship is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu IST Tutorship is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu IST Tutorship.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixedu.tutorship.ui.renderers.providers;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.fenixedu.academic.domain.Degree;

import pt.ist.fenixWebFramework.rendererExtensions.converters.DomainObjectKeyConverter;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixedu.tutorship.domain.Tutorship;
import pt.ist.fenixedu.tutorship.dto.teacher.tutor.StudentsPerformanceInfoBean;

public class TeacherDepartmentDegreesProvider implements DataProvider {

    @Override
    public Object provide(Object source, Object currentValue) {
        return getDegrees((StudentsPerformanceInfoBean) source);
    }

    static public Set<Degree> getDegrees(StudentsPerformanceInfoBean bean) {
        Set<Degree> degrees = new TreeSet<Degree>(Collections.reverseOrder(Degree.COMPARATOR_BY_FIRST_ENROLMENTS_PERIOD_AND_ID));

        List<Tutorship> tutorships = bean.getTutorships();

        for (Tutorship tutorship : tutorships) {
            degrees.add(tutorship.getStudentCurricularPlan().getRegistration().getDegree());
        }
        return degrees;
    }

    @Override
    public Converter getConverter() {
        return new DomainObjectKeyConverter();
    }

}
