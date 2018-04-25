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
public class NmapPortElement {

    private String protocol;
    private Integer portid;
    private NmapPortState state;
    private NmapService service;

    public NmapPortElement() {
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getPortid() {
        return portid;
    }

    public void setPortid(Integer portid) {
        this.portid = portid;
    }

    public NmapPortState getState() {
        return (state != null) ? state : new NmapPortState();
    }

    public void setState(NmapPortState state) {
        this.state = state;
    }

    public NmapService getService() {
        return (service != null) ? service : new NmapService();
    }

    public void setService(NmapService service) {
        this.service = service;
    }

}
