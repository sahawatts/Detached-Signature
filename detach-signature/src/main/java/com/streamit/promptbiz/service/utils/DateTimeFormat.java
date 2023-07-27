package com.streamit.promptbiz.service.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class DateTimeFormat {
    
    public String getCurrentDTTMString() {

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        return dateFormat.format(now);
    }
}