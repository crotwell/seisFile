package edu.sc.seis.seisFile.client;

import java.time.Instant;

import edu.sc.seis.seisFile.fdsnws.IRISWSVirtualNetworkQueryParams;
import picocli.CommandLine.Option;

public class IRISVirtualNetworkCmdLineQueryParams {
    

    IRISWSVirtualNetworkQueryParams queryParams;

    public IRISVirtualNetworkCmdLineQueryParams() {
        this(IRISWSVirtualNetworkQueryParams.DEFAULT_HOST);
    }

    public IRISVirtualNetworkCmdLineQueryParams(String host) {
        this.queryParams = new IRISWSVirtualNetworkQueryParams();
        setHost(host==null ? IRISWSVirtualNetworkQueryParams.DEFAULT_HOST : host);
    }

    @Option(names = { "--host" }, description="host to connect to, defaults to ${DEFAULT-VALUE}", defaultValue=IRISWSVirtualNetworkQueryParams.DEFAULT_HOST)
    public IRISWSVirtualNetworkQueryParams setHost(String host) {
        return this.queryParams.setHost(host);
    }

    @Option(names = "--port", description = "port to connect to, defaults to ${DEFAULT-VALUE}", defaultValue="80")
    public IRISWSVirtualNetworkQueryParams setPort(int port) {
        return this.queryParams.setPort(port);
    }


    /** Limit to metadata epochs starting on or after the specified start time.
     */
    @Option(names = { "-b","--starttime","--start" }, description="Limit to metadata epochs starting on or after the specified start time.", converter=FloorISOTimeParser.class)
    public IRISWSVirtualNetworkQueryParams setStartTime(Instant value) {
        queryParams.setStartTime(value);
        return queryParams;
    }


    /** Limit to metadata epochs ending on or before the specified end time.
     */
    @Option(names = { "-e","--endtime","--end" }, description="Limit to metadata epochs ending on or before the specified end time.", converter=CeilingISOTimeParser.class)
    public IRISWSVirtualNetworkQueryParams setEndTime(Instant value) {
        queryParams.setEndTime(value);
        return queryParams;
    }

    @Option(names = { "-c","--code"}, description="Virtual network code. Virtual network codes start with an underscore _")
    public IRISWSVirtualNetworkQueryParams setCode(String value) {
      queryParams.setCode(value);
      return queryParams;
    }

    @Option(names = { "-f","--format"}, description="Specifies result format, XML or CSV. Defaults to ${DEFAULT_VALUE}", defaultValue="XML")
    public IRISWSVirtualNetworkQueryParams setFormat(String value) {
      queryParams.setFormat(value);
      return queryParams;
    }

    @Option(names = { "-d","--definition"}, description="If true returns the virtual network definition, which can include wildcards. Otherwise, wildcards are expanded. Defaults to ${DEFAULT_VALUE}", defaultValue="true")
    public IRISWSVirtualNetworkQueryParams setFormat(boolean value) {
      queryParams.setDefinition(value);
      return queryParams;
    }


}
