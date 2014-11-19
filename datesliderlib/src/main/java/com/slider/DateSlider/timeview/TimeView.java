/*
 * Copyright (C) 2013 Adam Colclough - Axial Exchange
 *
 * This interface represents Views that are put onto the ScrollLayout
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

package com.slider.DateSlider.timeview;

import com.slider.DateSlider.TimeObject;

/**
 * This interface represents the views that will be placed in the ScrollLayout.
 * Each TimeView represents a visible element in the ScrollLayout, and is
 * displayed as a single point in time (e.g. January), but actually represents a
 * range of times (e.g. 1/1-1/31). The TimeView stores the range as well as
 * a string describing how to display itself.
 */
public interface TimeView {
    /**
     * Sets this TimeView to display the contents of the specified TimeObject.
     *
     * @param to The TimeObject to use to populate this TimeView
     */
    public void setTime(TimeObject to);

    public TimeObject getTimeObject();

}
