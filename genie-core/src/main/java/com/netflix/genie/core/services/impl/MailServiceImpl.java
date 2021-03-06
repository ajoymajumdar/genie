/*
 *
 *  Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.core.services.impl;

import com.netflix.genie.common.exceptions.GenieException;
import com.netflix.genie.common.exceptions.GenieServerException;
import com.netflix.genie.core.services.MailService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Implementation of the Mail service interface.
 *
 * @author amsharma
 * @since 3.0.0
 */
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final String fromAddress;

    /**
     * Constructor.
     *
     * @param javaMailSender An implementation of the JavaMailSender interface.
     * @param fromAddress The from email address for the email.
     */
    public MailServiceImpl(final JavaMailSender javaMailSender, final String fromAddress) {
        this.javaMailSender = javaMailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendEmail(
        @NotBlank(message = "Cannot send email to blank address.")
        final String toEmail,
        @NotBlank(message = "Subject cannot be empty")
        final String subject,
        final String body
    ) throws GenieException {
        final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(toEmail);
        simpleMailMessage.setFrom(this.fromAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);

        // check if body is not empty
        if (StringUtils.isNotBlank(body)) {
            simpleMailMessage.setText(body);
        }

        try {
            this.javaMailSender.send(simpleMailMessage);
        } catch (final MailException me) {
            throw new GenieServerException("Failure to send email: " + me);
        }
    }
}
