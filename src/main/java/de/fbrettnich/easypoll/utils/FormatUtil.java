/*
 * Copyright (c) 2021 Felix Brettnich
 *
 * This file is part of EasyPoll (https://github.com/fbrettnich/easypoll)
 *
 * All contents of this source code are protected by copyright.
 * The copyright lies, if not expressly differently marked,
 * by Felix Brettnich. All rights reserved.
 *
 * Any kind of duplication, distribution, rental, lending,
 * public accessibility or other use requires the explicit,
 * written consent from Felix Brettnich
 */

package de.fbrettnich.easypoll.utils;

import java.text.DecimalFormat;

public class FormatUtil {

    /**
     * Format number to decimal number with '.'
     *
     * @param number custom int
     * @return number in decimal format with '.'
     */
    public static String decimalFormat(int number) {
        DecimalFormat decimalFormat = new DecimalFormat();
        return decimalFormat.format(number).replace(",", ".");
    }

}
