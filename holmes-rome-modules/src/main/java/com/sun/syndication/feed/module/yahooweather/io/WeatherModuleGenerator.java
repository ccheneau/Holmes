/*
 * WeatherModuleGenerator.java
 *
 * This library is provided under dual licenses.
 * You may choose the terms of the Lesser General Public License or the Apache
 * License at your discretion.
 *
 *  Copyright (C) 2008  Robert Cooper, Temple of the Screaming Penguin
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
package com.sun.syndication.feed.module.yahooweather.io;

import java.text.SimpleDateFormat;
import java.util.HashSet;

import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.yahooweather.YWeatherModule;
import com.sun.syndication.feed.module.yahooweather.YWeatherModuleImpl;
import com.sun.syndication.feed.module.yahooweather.types.Forecast;
import com.sun.syndication.io.ModuleGenerator;

/** The ModuleGenerator implementation for the Yahoo Weather plug in.
 * @version $Revision: 1.3 $
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class WeatherModuleGenerator implements ModuleGenerator {
    private static final Namespace NS = Namespace.getNamespace("yweather", YWeatherModule.URI);
    private static final String TIME_ONLY = "h:mm a";
    private static final String LONG_DATE = "EEE, d MMM yyyy h:mm a zzz";
    private static final String SHORT_DATE = "d MMM yyyy";

    /** Creates a new instance of SlashModuleGenerator */
    public WeatherModuleGenerator() {
    }

    @Override
    public void generate(Module module, Element element) {
        if (!(module instanceof YWeatherModuleImpl)) {
            return;
        }

        YWeatherModuleImpl weather = (YWeatherModuleImpl) module;

        if (weather.getAstronomy() != null) {
            Element astro = new Element("astronomy", WeatherModuleGenerator.NS);

            if (weather.getAstronomy().getSunrise() != null) {
                astro.setAttribute("sunrise", new SimpleDateFormat(TIME_ONLY).format(weather.getAstronomy().getSunrise()).toLowerCase());
            }

            if (weather.getAstronomy().getSunrise() != null) {
                astro.setAttribute("sunset", new SimpleDateFormat(TIME_ONLY).format(weather.getAstronomy().getSunset()).toLowerCase());
            }

            element.addContent(astro);
        }

        if (weather.getAtmosphere() != null) {
            Element atmos = new Element("atmosphere", WeatherModuleGenerator.NS);
            atmos.setAttribute("humidity", Integer.toString(weather.getAtmosphere().getHumidity()));
            atmos.setAttribute("visibility", Integer.toString((int) (weather.getAtmosphere().getVisibility() * 100d)));
            atmos.setAttribute("pressure", Double.toString(weather.getAtmosphere().getPressure()));

            if (weather.getAtmosphere().getChange() != null) {
                atmos.setAttribute("rising", Integer.toString(weather.getAtmosphere().getChange().getCode()));
            }

            element.addContent(atmos);
        }

        if (weather.getCondition() != null) {
            Element condition = new Element("condition", WeatherModuleGenerator.NS);

            if (weather.getCondition().getText() != null) {
                condition.setAttribute("text", weather.getCondition().getText());
            }

            if (weather.getCondition().getCode() != null) {
                condition.setAttribute("code", Integer.toString(weather.getCondition().getCode().getCode()));
            }

            if (weather.getCondition().getDate() != null) {
                condition.setAttribute("date",
                        new SimpleDateFormat(LONG_DATE).format(weather.getCondition().getDate()).replaceAll("AM", "am").replaceAll("PM", "pm"));
            }

            condition.setAttribute("temp", Integer.toString(weather.getCondition().getTemperature()));
            element.addContent(condition);
        }

        if (weather.getLocation() != null) {
            Element location = new Element("location", WeatherModuleGenerator.NS);

            if (weather.getLocation().getCity() != null) {
                location.setAttribute("city", weather.getLocation().getCity());
            }

            if (weather.getLocation().getRegion() != null) {
                location.setAttribute("region", weather.getLocation().getRegion());
            }

            if (weather.getLocation().getCountry() != null) {
                location.setAttribute("country", weather.getLocation().getCountry());
            }

            element.addContent(location);
        }

        if (weather.getUnits() != null) {
            Element units = new Element("units", WeatherModuleGenerator.NS);

            if (weather.getUnits().getDistance() != null) {
                units.setAttribute("distance", weather.getUnits().getDistance());
            }

            if (weather.getUnits().getPressure() != null) {
                units.setAttribute("pressure", weather.getUnits().getPressure());
            }

            if (weather.getUnits().getSpeed() != null) {
                units.setAttribute("speed", weather.getUnits().getSpeed());
            }

            if (weather.getUnits().getTemperature() != null) {
                units.setAttribute("temperature", weather.getUnits().getTemperature());
            }

            element.addContent(units);
        }

        if (weather.getWind() != null) {
            Element wind = new Element("wind", WeatherModuleGenerator.NS);
            wind.setAttribute("chill", Integer.toString(weather.getWind().getChill()));
            wind.setAttribute("direction", Integer.toString(weather.getWind().getDirection()));
            wind.setAttribute("speed", Integer.toString(weather.getWind().getSpeed()));
            element.addContent(wind);
        }

        if (weather.getForecasts() != null) {
            for (int i = 0; i < weather.getForecasts().length; i++) {
                Element forecast = new Element("forecast", WeatherModuleGenerator.NS);
                Forecast f = weather.getForecasts()[i];

                if (f.getCode() != null) {
                    forecast.setAttribute("code", Integer.toString(f.getCode().getCode()));
                }

                if (f.getDate() != null) {
                    forecast.setAttribute("date", new SimpleDateFormat(SHORT_DATE).format(f.getDate()));
                }

                if (f.getDay() != null) {
                    forecast.setAttribute("day", f.getDay());
                }

                if (f.getText() != null) {
                    forecast.setAttribute("text", f.getText());
                }

                forecast.setAttribute("high", Integer.toString(f.getHigh()));
                forecast.setAttribute("low", Integer.toString(f.getLow()));
                element.addContent(forecast);
            }
        }
    }

    protected Element generateSimpleElement(String name, String value) {
        Element element = new Element(name, WeatherModuleGenerator.NS);
        element.addContent(value);

        return element;
    }

    @Override
    public java.util.Set<Namespace> getNamespaces() {
        HashSet<Namespace> set = new HashSet<Namespace>();
        set.add(WeatherModuleGenerator.NS);

        return set;
    }

    @Override
    public String getNamespaceUri() {
        return YWeatherModule.URI;
    }
}
