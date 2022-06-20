/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.apriori;


import java.lang.reflect.Constructor;

/*
 * Copyright 2017 Michael Rapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.mrapp.apriori.Item;


/**
 * An implementation of the type {@link Item}, which is used for test purposes. Each item can
 * unambiguously be identified via its name.
 *
 * @author Michael Rapp
 */
public class NamedItem implements Item {

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The name of the item.
     */
    private final String name;

    /**
     * Creates a new implementation of the type {@link Item}.
     *
     * @param name The name of the item as a {@link String}. The name may neither be null, nor
     *             empty
     */
    public NamedItem(@NotNull final String name) {
        ensureNotNull(name, "The name may not be null");
        ensureNotEmpty(name, "The name may not be empty");
        this.name = name;
    }

    /**
     * Returns the name of the item.
     *
     * @return The name of the item as a {@link String}. The name may neither be null, nor empty
     */
    @NotNull
    public final String getName() {
        return name;
    }

    @Override
    public final int compareTo(@NotNull final Item o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public final String toString() {
        return getName();
    }


    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NamedItem other = (NamedItem) obj;
        return name.equals(other.name);
    }
    
	public static void ensureNotNull(final Object object, @Nullable final String exceptionMessage) {
		ensureNotNull(object, exceptionMessage, IllegalArgumentException.class);
	}

	public static void ensureNotNull(final Object object, @Nullable final String exceptionMessage,
			@NotNull final Class<? extends RuntimeException> exceptionClass) {
		if (object == null) {
			throwException(exceptionMessage, exceptionClass);
		}
	}
	
	private static void throwException(@Nullable final String exceptionMessage,
			@NotNull final Class<? extends RuntimeException> exceptionClass) {
		RuntimeException exception;

		try {
			Constructor<? extends RuntimeException> constructor = exceptionClass.getConstructor(String.class);
			exception = constructor.newInstance(exceptionMessage);
		} catch (Exception e) {
			exception = new RuntimeException(exceptionMessage);
		}

		throw exception;
	}
	
	public static void ensureNotEmpty(final CharSequence text,
            @Nullable final String exceptionMessage) {
		ensureNotEmpty(text, exceptionMessage, IllegalArgumentException.class);
	}
	
	public static void ensureNotEmpty(final CharSequence text,
            @Nullable final String exceptionMessage,
            @NotNull final Class<? extends RuntimeException> exceptionClass) {
		if (text == null || text.length() == 0) {
			throwException(exceptionMessage, exceptionClass);
		}
	}

}
