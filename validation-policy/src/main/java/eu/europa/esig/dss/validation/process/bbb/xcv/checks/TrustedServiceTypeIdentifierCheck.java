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
package eu.europa.esig.dss.validation.process.bbb.xcv.checks;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import eu.europa.esig.dss.detailedreport.jaxb.XmlXCV;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.TrustedServiceWrapper;
import eu.europa.esig.dss.enumerations.Context;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.policy.jaxb.MultiValuesConstraint;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.process.AdditionalInfo;
import eu.europa.esig.dss.validation.process.bbb.AbstractMultiValuesCheckItem;

public class TrustedServiceTypeIdentifierCheck extends AbstractMultiValuesCheckItem<XmlXCV> {

	private final CertificateWrapper certificate;
	private final Date usageTime; // timestamp / revocation production date
	private final Context context;

	private String serviceTypeStr;

	public TrustedServiceTypeIdentifierCheck(XmlXCV result, CertificateWrapper certificate, Date usageTime, Context context, MultiValuesConstraint constraint) {
		super(result, constraint);
		this.certificate = certificate;
		this.usageTime = usageTime;
		this.context = context;
	}

	@Override
	protected boolean process() {
		// do not include Trusted list
		if (certificate.isCertificateChainFromTrustedStore()) {
			return true;
		}

		List<TrustedServiceWrapper> trustedServices = certificate.getTrustedServices();
		for (TrustedServiceWrapper trustedService : trustedServices) {
			serviceTypeStr = Utils.trim(trustedService.getType());
			Date statusStartDate = trustedService.getStartDate();
			if (processValueCheck(serviceTypeStr) && statusStartDate != null) {
				Date statusEndDate = trustedService.getEndDate();
				// The issuing time of the certificate should be into the validity period of the associated
				// service
				if ((usageTime.compareTo(statusStartDate) >= 0) && ((statusEndDate == null) || usageTime.before(statusEndDate))) {
					return true;
				}
			}
		}
		return false;

	}

	@Override
	protected String getAdditionalInfo() {
		if (Utils.isStringNotEmpty(serviceTypeStr)) {
			Object[] params = new Object[] { serviceTypeStr };
			return MessageFormat.format(AdditionalInfo.TRUSTED_SERVICE_TYPE, params);
		}
		return null;
	}

	@Override
	protected String getMessageTag() {
		return "XCV_TSL_ETIP";
	}

	@Override
	protected String getErrorMessageTag() {
		switch (context) {
		case SIGNATURE:
			return "XCV_TSL_ETIP_SIG_ANS";
		case COUNTER_SIGNATURE:
			return "XCV_TSL_ETIP_SIG_ANS";
		case TIMESTAMP:
			return "XCV_TSL_ETIP_TSP_ANS";
		case REVOCATION:
			return "XCV_TSL_ETIP_REV_ANS";
		default:
			return "XCV_TSL_ETIP_ANS";
		}
	}

	@Override
	protected Indication getFailedIndicationForConclusion() {
		return Indication.INDETERMINATE;
	}

	@Override
	protected SubIndication getFailedSubIndicationForConclusion() {
		return SubIndication.NO_CERTIFICATE_CHAIN_FOUND;
	}

}
