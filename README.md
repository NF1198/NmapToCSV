# NmapToCSV
Command-line tool for parsing NMAP output (XML) and generating a CSV file with 
hosts and services (can process entire directories of XML files into a single CSV)

Build with gradle.

Inputs: Directory of NMAP output files, or individual files
Outputs: CSV containing all hosts, ports, and services found

[Distributions](https://github.com/NF1198/NmapToCSV/wiki)

## Example Usage

    nmap2csv <sub-command> <sub-command arguments>

    usage [-f <arg>]
    ======================================
    usage arguments:
    -f,--format <arg>  output format [text, html]

    exportHosts [-D <arg>] [-i <arg>] [-v]
    ======================================
    exportHosts arguments:
    -D,--directory <arg>
    -i,--input <arg>
    -v,--verbose          verbose logging

    $> nmap2csv exportHosts -D . > hosts_summary.csv

Please contact me if you have questions about how to use this project.

# Example output

    $> nmap2csv exportHosts -D <path to NMAP XML> | column -s, -t
    exportHosts
    IPv4           hostname  service           port   proto  state   product
    192.168.1.1              domain            53     tcp    open    dnsmasq
    192.168.1.1              http              80     tcp    open    GoAhead WebServer
    192.168.1.1              pptp              1723   tcp    open
    192.168.1.115            ssh               22     tcp    open    OpenSSH
    192.168.1.115            http              80     tcp    open    Apache httpd
    192.168.1.115            netbios-ssn       139    tcp    open    Samba smbd
    192.168.1.115            netbios-ssn       445    tcp    open    Samba smbd
    192.168.1.185            msrpc             135    tcp    open    Microsoft Windows RPC
    192.168.1.185            netbios-ssn       139    tcp    open    Microsoft Windows netbios-ssn
    192.168.1.185            microsoft-ds      445    tcp    open
    192.168.1.185            http              5357   tcp    open    Microsoft HTTPAPI httpd
    192.168.1.214            unknown           6646   tcp    closed
    192.168.1.214            realserver        7070   tcp    closed
    192.168.1.214            http-alt          8000   tcp    closed
    192.168.1.214            http              8008   tcp    closed
    192.168.1.214            ajp13             8009   tcp    closed
    192.168.1.214            http-proxy        8080   tcp    closed
    192.168.1.214            blackice-icecap   8081   tcp    closed
    192.168.1.214            https-alt         8443   tcp    closed
    192.168.1.214            sun-answerbook    8888   tcp    closed
    192.168.1.214            jetdirect         9100   tcp    closed
    192.168.1.214            abyss             9999   tcp    closed
    192.168.1.214            snet-sensor-mgmt  10000  tcp    closed
    192.168.1.33             ssh               22     tcp    open    OpenSSH
    192.168.1.33             http              80     tcp    open    Apache httpd
