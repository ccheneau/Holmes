/*
 * PaymentTypeEnumeration.java
 *
 * Created on November 16, 2005, 11:49 AM
 *
 * This library is provided under dual licenses.
 * You may choose the terms of the Lesser General Public License or the Apache
 * License at your discretion.
 *
 *  Copyright (C) 2005  Robert Cooper, Temple of the Screaming Penguin
 *
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sun.syndication.feed.module.base.types;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.1 $
 */
public final class PaymentTypeEnumeration implements Cloneable, Serializable {
    private static final long serialVersionUID = 6444069283656132873L;

    private static final Map<String, PaymentTypeEnumeration> LOOKUP = new HashMap<String, PaymentTypeEnumeration>();
    public static final PaymentTypeEnumeration CASH = new PaymentTypeEnumeration("Cash");
    public static final PaymentTypeEnumeration CHECK = new PaymentTypeEnumeration("Check");
    public static final PaymentTypeEnumeration TRAVELERS_CHECK = new PaymentTypeEnumeration("Traveler�s Check");
    public static final PaymentTypeEnumeration VISA = new PaymentTypeEnumeration("Visa");
    public static final PaymentTypeEnumeration MASTERCARD = new PaymentTypeEnumeration("MasterCard");
    public static final PaymentTypeEnumeration AMERICAN_EXPRESS = new PaymentTypeEnumeration("American Express");
    public static final PaymentTypeEnumeration DISCOVER = new PaymentTypeEnumeration("Discover");
    public static final PaymentTypeEnumeration WIRE_TRANSFER = new PaymentTypeEnumeration("Wire transfer");
    public static final PaymentTypeEnumeration PAYPAL = new PaymentTypeEnumeration("Paypal");
    private String value;

    /**
     * Creates a new instance of PaymentTypeEnumeration.
     *
     * @param value the value
     */
    private PaymentTypeEnumeration(final String value) {
        this.value = value;
        LOOKUP.put(this.value.toUpperCase(), this);
    }

    /**
     * Find by value.
     *
     * @param value the value
     * @return payment type enumeration
     */
    public static PaymentTypeEnumeration findByValue(final String value) {
        return LOOKUP.get(value.toUpperCase());
    }

    public String getValue() {
        return value;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        return this;
    }

    @Override
    public String toString() {
        return value;
    }
}
