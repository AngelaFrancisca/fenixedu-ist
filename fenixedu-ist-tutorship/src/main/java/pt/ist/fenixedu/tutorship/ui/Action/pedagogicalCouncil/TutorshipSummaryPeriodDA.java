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
package pt.ist.fenixedu.tutorship.ui.Action.pedagogicalCouncil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

@Mapping(path = "/tutorshipSummaryPeriod", module = "pedagogicalCouncil", functionality = TutorshipSummaryDA.class)
@Forwards({ @Forward(name = "createPeriod", path = "/pedagogicalCouncil/tutorship/createPeriod.jsp"),
        @Forward(name = "confirmMessagePeriod", path = "/pedagogicalCouncil/tutorship/confirmCreatePeriod.jsp") })
public class TutorshipSummaryPeriodDA extends FenixDispatchAction {

    public ActionForward prepareCreate(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        RenderUtils.invalidateViewState();

        TutorshipSummaryPeriodBean bean = new TutorshipSummaryPeriodBean();
        setTutorshipSummaryPeriodBean(request, bean);

        return mapping.findForward("createPeriod");
    }

    public ActionForward prepareCreate2(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        TutorshipSummaryPeriodBean bean = getTutorshipSummaryPeriodBean();

        if (bean == null) {
            return prepareCreate(mapping, actionForm, request, response);
        }

        setTutorshipSummaryPeriodBean(request, bean);

        return mapping.findForward("createPeriod");
    }

    public ActionForward create(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        TutorshipSummaryPeriodBean bean = getTutorshipSummaryPeriodBean();

        if (bean != null && bean.isValid()) {
            bean.save();

            return mapping.findForward("confirmMessagePeriod");
        }

        return prepareCreate2(mapping, actionForm, request, response);
    }

    protected TutorshipSummaryPeriodBean getTutorshipSummaryPeriodBean() {
        return getRenderedObject("periodBean");
    }

    protected void setTutorshipSummaryPeriodBean(HttpServletRequest request, TutorshipSummaryPeriodBean bean) {
        request.setAttribute("periodBean", bean);
    }
}
