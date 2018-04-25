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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nicholas Folse
 */
public class NmapHostElement {

    private String comment = null;
    private String status = null;
    private final List<NmapHostname> hostnames = new ArrayList<>();
    private final List<NmapAddressElement> addresses = new ArrayList<>();
    private final List<NmapPortElement> ports = new ArrayList<>();
    private NmapOSElement os;

    private NmapUptime uptime;

    public NmapHostElement() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public NmapOSElement getOs() {
        return os;
    }

    public void setOs(NmapOSElement os) {
        this.os = os;
    }

    public NmapUptime getUptime() {
        return uptime;
    }

    public void setUptime(NmapUptime uptime) {
        this.uptime = uptime;
    }

    public List<NmapAddressElement> getAddresses() {
        return addresses;
    }

    public List<NmapPortElement> getPorts() {
        return ports;
    }

    public List<NmapHostname> getHostnames() {
        return hostnames;
    }

}
