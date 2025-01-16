package sk.tuke.service;

import sk.tuke.service.dto.EmailDetails;

public interface EmailService {

    boolean sendSimpleMail(EmailDetails details);}
