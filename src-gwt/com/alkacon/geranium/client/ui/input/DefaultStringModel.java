/*
 * This library is part of Geranium -
 * an open source UI library for GWT.
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)-
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.geranium.client.ui.input;

import com.google.common.base.Objects;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;

/**
 * The default string model implementation.<p>
 */
public class DefaultStringModel implements I_StringModel {

    /** The event bus for this class. */
    EventBus m_eventBus = new SimpleEventBus();

    /**
     * A flag which indicates that the model is currently being updated, which is used to prevent infinite recursion.<p>
     */
    private boolean m_active;

    /** The id. */
    private String m_id;

    /** The model value. */
    private String m_value;

    /**
     * Creates a new string model.<p>
     * 
     * @param id the model id 
     */
    public DefaultStringModel(String id) {

        m_id = id;
        m_value = "";
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {

        return m_eventBus.addHandler(ValueChangeEvent.getType(), handler);
    }

    /**
     * @see com.google.gwt.event.shared.HasHandlers#fireEvent(com.google.gwt.event.shared.GwtEvent)
     */
    public void fireEvent(GwtEvent<?> event) {

        m_eventBus.fireEvent(event);
    }

    /**
     * @see com.alkacon.geranium.client.ui.input.I_StringModel#getId()
     */
    public String getId() {

        return m_id;
    }

    /**
     * @see com.alkacon.geranium.client.ui.input.I_StringModel#getValue()
     */
    public String getValue() {

        return m_value;
    }

    /**
     * @see com.alkacon.geranium.client.ui.input.I_StringModel#setValue(java.lang.String, boolean)
     */
    public void setValue(String value, boolean notify) {

        if (!m_active) {
            m_active = true;
            try {

                boolean changed = !Objects.equal(value, m_value);
                m_value = value;
                if (notify && changed) {
                    ValueChangeEvent.fire(this, value);
                }
            } finally {
                m_active = false;
            }
        }

    }

}
