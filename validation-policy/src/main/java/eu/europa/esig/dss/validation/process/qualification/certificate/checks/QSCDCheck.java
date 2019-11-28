/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.validation.process.qualification.certificate.checks;

import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationCertificateQualification;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.enumerations.ValidationTime;
import eu.europa.esig.dss.policy.jaxb.LevelConstraint;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.qualification.certificate.QSCDStatus;

public class QSCDCheck extends ChainItem<XmlValidationCertificateQualification> {

	private final QSCDStatus qscdStatus;
	private final ValidationTime validationTime;

	public QSCDCheck(XmlValidationCertificateQualification result, QSCDStatus qscdStatus, ValidationTime validationTime, LevelConstraint constraint) {
		super(result, constraint);

		this.qscdStatus = qscdStatus;
		this.validationTime = validationTime;
	}

	@Override
	protected boolean process() {
		return QSCDStatus.isQSCD(qscdStatus);
	}

	@Override
	protected String getMessageTag() {
		switch (validationTime) {
		case BEST_SIGNATURE_TIME:
			return "QUAL_QSCD_AT_ST";
		case CERTIFICATE_ISSUANCE_TIME:
			return "QUAL_QSCD_AT_CC";
		case VALIDATION_TIME:
			return "QUAL_QSCD_AT_VT";
		default:
			throw new IllegalArgumentException("Unsupported time " + validationTime);
		}
	}

	@Override
	protected String getErrorMessageTag() {
		switch (validationTime) {
		case BEST_SIGNATURE_TIME:
			return "QUAL_QSCD_AT_ST_ANS";
		case CERTIFICATE_ISSUANCE_TIME:
			return "QUAL_QSCD_AT_CC_ANS";
		case VALIDATION_TIME:
			return "QUAL_QSCD_AT_VT_ANS";
		default:
			throw new IllegalArgumentException("Unsupported time " + validationTime);
		}
	}

	@Override
	protected Indication getFailedIndicationForConclusion() {
		return Indication.FAILED;
	}

	@Override
	protected SubIndication getFailedSubIndicationForConclusion() {
		return null;
	}

}
