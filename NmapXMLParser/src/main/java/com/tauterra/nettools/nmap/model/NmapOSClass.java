/*
 * Copyright 2018 Nicholas Folse.
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
package com.tauterra.nettools.nmap.model;

/**
 *
 * @author Nicholas Folse
 */
public class NmapOSClass {

    private String type;
    private String osfamily;
    private String vendor;
    private String osgen;
    private Integer accuracy;

    public NmapOSClass() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOsfamily() {
        return osfamily;
    }

    public void setOsfamily(String osfamily) {
        this.osfamily = osfamily;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getOsgen() {
        return osgen;
    }

    public void setOsgen(String osgen) {
        this.osgen = osgen;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }
    
    
}
