package com.github.richardwilly98.esdms.api;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.RatingImpl;

/*
 * #%L
 * es-dms-core
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

@JsonDeserialize(as = RatingImpl.class)
public interface Rating {
    // /*
    // * Item id of the rating
    // */
    // public abstract String getItemId();

    /*
     * user login who has created the rating
     */
    public abstract String getUser();

    /*
     * Date of the rating
     */
    public abstract Date getDate();

    /*
     * Score of the rating (between 1 and 5)
     */
    public abstract int getScore();
}
