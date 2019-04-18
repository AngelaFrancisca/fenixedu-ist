/**
 * Copyright © 2013 Instituto Superior Técnico
 *
 * This file is part of FenixEdu IST QUC.
 *
 * FenixEdu IST QUC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu IST QUC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu IST QUC.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package pt.ist.fenixedu.quc.ui.renderers;

import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlLink;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableCell;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableRow;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixedu.quc.domain.InquiryDelegateAnswer;
import pt.ist.fenixedu.quc.dto.BlockResumeResult;
import pt.ist.fenixedu.quc.dto.CurricularCourseResumeResult;

/**
 * @author - Ricardo Rodrigues (ricardo.rodrigues@ist.utl.pt)
 * 
 */
public class InquiryCourseDegreesResumeRenderer extends InquiryBlocksResumeRenderer {

    @Override
    protected void createFinalCells(HtmlTableRow tableRow, BlockResumeResult blockResumeResult) {

        HtmlTableCell linksCell = tableRow.createCell();
        String resultsParameters = buildParametersForResults(blockResumeResult);

        HtmlInlineContainer container = new HtmlInlineContainer();

        HtmlLink resultsLink = new HtmlLink();
        resultsLink.setModule("/publico");
        resultsLink.setUrl("/viewCourseResults.do?" + resultsParameters);
        resultsLink.setTarget("_blank");
        resultsLink.setText("Resultados");

        container.addChild(resultsLink);

        CurricularCourseResumeResult courseResume = (CurricularCourseResumeResult) blockResumeResult;
        for (InquiryDelegateAnswer inquiryDelegateAnswer : courseResume.getExecutionCourse()
                .getInquiryDelegatesAnswersSet()) {
            if (inquiryDelegateAnswer.getExecutionDegree() == courseResume.getExecutionDegree()) {
                String delegateInquiryParameters = buildParametersForDelegateInquiry(inquiryDelegateAnswer);

                HtmlLink delegateLink = new HtmlLink();
                delegateLink.setModule("/publico");
                delegateLink.setUrl("/viewQUCInquiryAnswers.do?method=showDelegateInquiry" + delegateInquiryParameters);
                delegateLink.setTarget("_blank");
                delegateLink.setText("Relatório " + inquiryDelegateAnswer.getDelegate().getTitle());
                container.addChild(new HtmlText("&nbsp;|&nbsp;", false));
                container.addChild(delegateLink);
            }
        }

        linksCell.setBody(container);
        linksCell.setClasses("col-actions");
    }

    private String buildParametersForDelegateInquiry(final InquiryDelegateAnswer inquiryDelegateAnswer) {
        StringBuilder builder = new StringBuilder();
        builder.append("&inquiryDelegateAnswerOID=").append(inquiryDelegateAnswer.getExternalId());
        return builder.toString();
    }

    private String buildParametersForResults(BlockResumeResult blocksResumeResult) {
        CurricularCourseResumeResult courseResume = (CurricularCourseResumeResult) blocksResumeResult;
        StringBuilder builder = new StringBuilder();
        builder.append("degreeCurricularPlanOID=").append(
                courseResume.getExecutionDegree().getDegreeCurricularPlan().getExternalId());
        builder.append("&executionCourseOID=").append(courseResume.getExecutionCourse().getExternalId());
        return builder.toString();
    }
}
