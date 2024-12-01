package com.fantasydrawer.ecommerce;

import org.apache.commons.lang3.StringUtils;

public class Example {
    public static void main(String[] args) {
        String input = "   ";
        
        // Using StringUtils from commons-lang3 to check if the string is empty
        if (StringUtils.isBlank(input)) {
            System.out.println("The string is blank or empty");
        }
    }
}

