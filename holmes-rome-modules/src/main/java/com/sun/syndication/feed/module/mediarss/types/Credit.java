/*
 * Copyright (C) 2012-2013  Cedric Cheneau
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sun.syndication.feed.module.mediarss.types;

import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ToStringBean;

import java.io.Serializable;

/**
 * <strong>&lt;media:credit&gt;</strong></p>
 * <p/>
 * <p>Notable entity and the contribution to the creation of the media object. Current entities can include people, companies, locations, etc. Specific entities can have multiple roles, and several entities can have the same role.
 * These should appear as distinct &lt;media:credit&gt; elements.
 * It has 2 optional attributes.</p>
 * <pre>        &lt;media:credit role="producer" scheme="urn:ebu"&gt;entity name&lt;/media:credit&gt;
 * </pre>
 * <p>role specifies the role the entity played. Must be lowercase. It is an optional attribute.</p>
 * <p/>
 * <p><em>scheme</em> is the URI that identifies the role scheme. It is an optional attribute. If this attribute is not included, the default scheme is 'urn:ebu'. See: European Broadcasting Union Role Codes.</p>
 * <p/>
 * <p/>
 * <p>Example roles:</p>
 * <pre>        actor
 *        anchor person
 *        author
 *        choreographer
 *        composer
 *        conductor
 *        director
 *        editor
 *        graphic designer
 *        grip
 *        illustrator
 *        lyricist
 *        music arranger
 *        music group
 *        musician
 *        orchestra
 *        performer
 *        photographer
 *        producer
 *        reporter
 *        vocalist
 * </pre>
 * <p>Additional roles: <a href="http://www.ebu.ch/en/technical/metadata/specifications/role_codes.php">European Broadcasting Union Role Codes</a>
 *
 * @author cooper
 */
public class Credit implements Serializable {
    private static final long serialVersionUID = 7722721287224043428L;
    /**
     * Scheme value for the EBU credits.
     */
    private static final String SCHEME_EBU = "urn:ebu";
    private String name;
    private String role;
    private String scheme;

    /**
     * Creates a new instance of Credit
     *
     * @param scheme scheme used
     * @param role   role name
     * @param name   persons name
     */
    public Credit(final String scheme, final String role, final String name) {
        if (name == null) {
            throw new NullPointerException("A credit name cannot be null.");
        }

        this.scheme = (scheme == null) ? SCHEME_EBU : scheme;
        this.role = (role == null) ? null : role.toLowerCase();
        this.name = name;
    }

    /**
     * Person/organizations name
     *
     * @return Person/organizations name
     */
    public String getName() {
        return name;
    }

    /**
     * Role name
     *
     * @return Role name
     */
    public String getRole() {
        return role;
    }

    /**
     * Scheme used.
     *
     * @return Scheme used.
     */
    public String getScheme() {
        return scheme;
    }

    @Override
    public boolean equals(final Object obj) {
        EqualsBean eBean = new EqualsBean(this.getClass(), this);
        return eBean.beanEquals(obj);
    }

    @Override
    public int hashCode() {
        EqualsBean equals = new EqualsBean(this.getClass(), this);
        return equals.beanHashCode();
    }

    @Override
    public String toString() {
        ToStringBean tsBean = new ToStringBean(this.getClass(), this);
        return tsBean.toString();
    }
}
