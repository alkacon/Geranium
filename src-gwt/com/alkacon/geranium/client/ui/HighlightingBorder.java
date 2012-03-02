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

package com.alkacon.geranium.client.ui;

import com.alkacon.geranium.client.ui.css.I_LayoutBundle;
import com.alkacon.geranium.client.util.PositionBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

/**
 * A Widget to display a highlighting border around a specified position.<p>
 */
public class HighlightingBorder extends Composite {

    /** Enumeration of available border colours. */
    public enum BorderColor {
        /** Color blue. */
        blue(I_LayoutBundle.INSTANCE.highlightCss().colorBlue()),

        /** Color red. */
        red(I_LayoutBundle.INSTANCE.highlightCss().colorRed());

        /** CSS class used to display the border colour. */
        private String m_cssClass;

        /**
         * Constructor.<p>
         * 
         * @param cssClass the CSS class to display the border colour
         */
        private BorderColor(String cssClass) {

            m_cssClass = cssClass;
        }

        /**
         * Returns the associated CSS class.<p>
         * 
         * @return the CSS class
         */
        public String getCssClass() {

            return m_cssClass;
        }
    }

    /** The ui-binder interface for this composite. */
    interface I_CmsHighlightingBorderUiBinder extends UiBinder<HTML, HighlightingBorder> {
        // GWT interface, nothing to do here
    }

    /** The border offset to the given position. */
    private static final int BORDER_OFFSET = 4;

    /** The border width. */
    private static final int BORDER_WIDTH = 2;

    /** The ui-binder instance. */
    private static I_CmsHighlightingBorderUiBinder uiBinder = GWT.create(I_CmsHighlightingBorderUiBinder.class);

    /** The bottom border. */
    @UiField
    protected DivElement m_borderBottom;

    /** The left border. */
    @UiField
    protected DivElement m_borderLeft;

    /** The right border. */
    @UiField
    protected DivElement m_borderRight;

    /** The top border. */
    @UiField
    protected DivElement m_borderTop;

    /**
     * Constructor.<p>
     * 
     * @param position the position data
     * @param color the border color
     */
    public HighlightingBorder(PositionBean position, BorderColor color) {

        this(position.getHeight(), position.getWidth(), position.getLeft(), position.getTop(), color);
    }

    /**
     * Constructor.<p>
     * 
     * @param height the height
     * @param width the width
     * @param positionLeft the absolute left position
     * @param positionTop the absolute top position
     * @param color the border color
     */
    public HighlightingBorder(int height, int width, int positionLeft, int positionTop, BorderColor color) {

        initWidget(uiBinder.createAndBindUi(this));

        getWidget().addStyleName(color.getCssClass());
        setPosition(height, width, positionLeft, positionTop);
    }

    /**
     * Hides the border.<p>
     */
    public void hide() {

        setVisible(false);
    }

    /**
     * Sets the border position.<p>
     * 
     * @param position the position data
     */
    public void setPosition(PositionBean position) {

        setPosition(position.getHeight(), position.getWidth(), position.getLeft(), position.getTop());
    }

    /**
     * Sets the border position.<p>
     * 
     * @param height the height
     * @param width the width
     * @param positionLeft the absolute left position
     * @param positionTop the absolute top position
     */
    public void setPosition(int height, int width, int positionLeft, int positionTop) {

        positionLeft -= BORDER_OFFSET;

        // make sure highlighting does not introduce additional horizontal scroll-bars
        if (positionLeft < 0) {
            // position left should not be negative
            width += positionLeft;
            positionLeft = 0;
        }
        width += (2 * BORDER_OFFSET) - BORDER_WIDTH;
        if ((Window.getClientWidth() < (width + positionLeft)) && (Window.getScrollLeft() == 0)) {
            // highlighting should not extend over the right hand 
            width = Window.getClientWidth() - (positionLeft + BORDER_WIDTH);
        }
        Style style = getElement().getStyle();
        style.setLeft(positionLeft, Unit.PX);
        style.setTop(positionTop - BORDER_OFFSET, Unit.PX);
        setHeight((height + (2 * BORDER_OFFSET)) - BORDER_WIDTH);
        setWidth(width);
    }

    /**
     * Shows the border.<p>
     */
    public void show() {

        setVisible(true);
    }

    /**
     * Sets the highlighting height.<p>
     * 
     * @param height the height
     */
    private void setHeight(int height) {

        m_borderRight.getStyle().setHeight(height, Unit.PX);
        m_borderLeft.getStyle().setHeight(height, Unit.PX);
        m_borderBottom.getStyle().setTop(height, Unit.PX);
    }

    /**
     * Sets the highlighting width.<p>
     * 
     * @param width the width
     */
    private void setWidth(int width) {

        m_borderTop.getStyle().setWidth(width, Unit.PX);
        m_borderBottom.getStyle().setWidth(width, Unit.PX);
        m_borderRight.getStyle().setLeft(width, Unit.PX);
    }

}
