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
public class NmapOSMatch {

    private Long line;
    private String name;
    private Integer accuracy;
    private NmapOSClass osclass;

    public NmapOSMatch() {
    }

    public Long getLine() {
        return line;
    }

    public void setLine(Long line) {
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public NmapOSClass getOsclass() {
        return osclass;
    }

    public void setOsclass(NmapOSClass osclass) {
        this.osclass = osclass;
    }

}
