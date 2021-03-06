/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.server.remote.rest.casemgmt;

import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.jbpm.casemgmt.api.model.CaseStatus;
import org.kie.server.api.model.cases.CaseDefinitionList;
import org.kie.server.api.model.cases.CaseFileDataItemList;
import org.kie.server.api.model.cases.CaseInstanceList;
import org.kie.server.api.model.definition.ProcessDefinitionList;
import org.kie.server.api.model.instance.TaskSummaryList;
import org.kie.server.remote.rest.common.Header;
import org.kie.server.services.api.KieServerRegistry;
import org.kie.server.services.casemgmt.CaseManagementRuntimeDataServiceBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.server.api.rest.RestURI.*;
import static org.kie.server.remote.rest.common.util.RestUtils.*;

@Path("server/" + CASE_QUERY_URI)
public class CaseQueryResource extends AbstractCaseResource {

    private static final Logger logger = LoggerFactory.getLogger(CaseQueryResource.class);

    public CaseQueryResource() {

    }

    public CaseQueryResource(
            final CaseManagementRuntimeDataServiceBase caseManagementRuntimeDataServiceBase,
            final KieServerRegistry context) {
        super(caseManagementRuntimeDataServiceBase, context);
    }

    @GET
    @Path(CASE_ALL_INSTANCES_GET_URI)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCaseInstances(@javax.ws.rs.core.Context HttpHeaders headers,
            @QueryParam("dataItemName") String dataItemName, @QueryParam("dataItemValue") String dataItemValue,
            @QueryParam("owner") String owner, @QueryParam("status") List<String> status,
            @QueryParam("page") @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
            @QueryParam("sort") String sort, @QueryParam("sortOrder") @DefaultValue("true") boolean sortOrder) {

        return invokeCaseOperation(headers,
                "",
                null,
                (Variant v, String type, Header... customHeaders) -> {

                    CaseInstanceList responseObject = null;
                    if (dataItemName != null && !dataItemName.isEmpty() && dataItemValue != null && !dataItemValue.isEmpty()) {
                        logger.debug("About to look for case instances by data item name {} and value {} with status {}", dataItemName, dataItemValue, status);
                        responseObject = this.caseManagementRuntimeDataServiceBase.getCaseInstancesByCaseFileData(dataItemName, dataItemValue, status, page, pageSize, sort, sortOrder);
                    } else if (dataItemName != null && !dataItemName.isEmpty()) {
                        logger.debug("About to look for case instances by data item name {} with status {}", dataItemName, dataItemValue, status);
                        responseObject = this.caseManagementRuntimeDataServiceBase.getCaseInstancesByCaseFileData(dataItemName, null, status, page, pageSize, sort, sortOrder);
                    } else if (owner != null && !owner.isEmpty()) {
                        logger.debug("About to look for case instances owned by {} with status {}", owner, status);
                        responseObject = this.caseManagementRuntimeDataServiceBase.getCaseInstancesOwnedBy(owner,
                                                                                                           status, page, pageSize, sort, sortOrder);
                    } else {
                        logger.debug("About to look for case instances with status {}", status);
                        responseObject = this.caseManagementRuntimeDataServiceBase.getCaseInstancesAnyRole(status, page, pageSize, sort, sortOrder);
                    }

                    logger.debug("Returning OK response with content '{}'", responseObject);
                    return createCorrectVariant(responseObject, headers, Response.Status.OK, customHeaders);
                });
    }

    @GET
    @Path(CASE_INSTANCES_BY_ROLE_GET_URI)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCaseInstancesByRole(@javax.ws.rs.core.Context HttpHeaders headers,
            @PathParam(CASE_ROLE_NAME) String roleName, @QueryParam("status") List<String> status,
            @QueryParam("page") @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
            @QueryParam("sort") String sort, @QueryParam("sortOrder") @DefaultValue("true") boolean sortOrder) {

        return invokeCaseOperation(headers,
                "",
                null,
                (Variant v, String type, Header... customHeaders) -> {

                    logger.debug("About to look for case instances with status {}", status);
                    CaseInstanceList responseObject = this.caseManagementRuntimeDataServiceBase.getCaseInstancesByRole(roleName, status, page, pageSize, sort, sortOrder);

                    logger.debug("Returning OK response with content '{}'", responseObject);
                    return createCorrectVariant(responseObject, headers, Response.Status.OK, customHeaders);
                });
    }

    /*
     * case definition methods
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCaseDefinitions(@javax.ws.rs.core.Context HttpHeaders headers,
            @QueryParam("filter") String filter,
            @QueryParam("page") @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
            @QueryParam("sort") String sort, @QueryParam("sortOrder") @DefaultValue("true") boolean sortOrder) {

        return invokeCaseOperation(headers,
                "",
                null,
                (Variant v, String type, Header... customHeaders) -> {

                    logger.debug("About to look for case definitions with filter {}", filter);
                    CaseDefinitionList responseObject = this.caseManagementRuntimeDataServiceBase.getCaseDefinitions(filter, page, pageSize, sort, sortOrder);

                    logger.debug("Returning OK response with content '{}'", responseObject);
                    return createCorrectVariant(responseObject, headers, Response.Status.OK, customHeaders);
                });
    }

    /*
     * process definition methods
     */
    @GET
    @Path(CASE_ALL_PROCESSES_INSTANCES_GET_URI)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getProcessDefinitions(@javax.ws.rs.core.Context HttpHeaders headers,
            @QueryParam("filter") String filter,
            @QueryParam("page") @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
            @QueryParam("sort") String sort, @QueryParam("sortOrder") @DefaultValue("true") boolean sortOrder) {

        return invokeCaseOperation(headers,
                "",
                null,
                (Variant v, String type, Header... customHeaders) -> {

                    logger.debug("About to look for process definitions with filter {}", filter);
                    ProcessDefinitionList responseObject = this.caseManagementRuntimeDataServiceBase.getProcessDefinitions(filter, null, page, pageSize, sort, sortOrder);

                    logger.debug("Returning OK response with content '{}'", responseObject);
                    return createCorrectVariant(responseObject, headers, Response.Status.OK, customHeaders);
                });
    }

    @GET
    @Path(CASE_PROCESSES_BY_CONTAINER_INSTANCES_GET_URI)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getProcessDefinitionsByContainer(@javax.ws.rs.core.Context HttpHeaders headers, @PathParam("id") String containerId,
            @QueryParam("page") @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
            @QueryParam("sort") String sort, @QueryParam("sortOrder") @DefaultValue("true") boolean sortOrder) {

        return invokeCaseOperation(headers,
                "",
                null,
                (Variant v, String type, Header... customHeaders) -> {

                    logger.debug("About to look for process definitions with container id {}", containerId);
                    ProcessDefinitionList responseObject = this.caseManagementRuntimeDataServiceBase.getProcessDefinitions(null, containerId, page, pageSize, sort, sortOrder);

                    logger.debug("Returning OK response with content '{}'", responseObject);
                    return createCorrectVariant(responseObject, headers, Response.Status.OK, customHeaders);
                });
    }

    /*
     * Case tasks
     */

    @GET
    @Path(CASE_TASKS_AS_POT_OWNER_GET_URI)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCaseInstanceTasksAsPotentialOwner(@javax.ws.rs.core.Context HttpHeaders headers,
            @PathParam(CASE_ID) String caseId,
            @QueryParam("user") String user, @QueryParam("status") List<String> status,
            @QueryParam("page") @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
            @QueryParam("sort") String sort, @QueryParam("sortOrder") @DefaultValue("true") boolean sortOrder) {

        return invokeCaseOperation(headers,
                "",
                null,
                (Variant v, String type, Header... customHeaders) -> {
                    logger.debug("About to look for case instance {} tasks with status {} assigned to potential owner {}", caseId, status, user);
                    TaskSummaryList responseObject = this.caseManagementRuntimeDataServiceBase.getCaseTasks(caseId, user, status, page, pageSize, sort, sortOrder);

                    logger.debug("Returning OK response with content '{}'", responseObject);
                    return createCorrectVariant(responseObject, headers, Response.Status.OK, customHeaders);
                });
    }

    @GET
    @Path(CASE_TASKS_AS_ADMIN_GET_URI)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCaseInstanceTasksAsAdmin(@javax.ws.rs.core.Context HttpHeaders headers,
            @PathParam(CASE_ID) String caseId,
            @QueryParam("user") String user, @QueryParam("status") List<String> status,
            @QueryParam("page") @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
            @QueryParam("sort") String sort, @QueryParam("sortOrder") @DefaultValue("true") boolean sortOrder) {

        return invokeCaseOperation(headers,
                "",
                null,
                (Variant v, String type, Header... customHeaders) -> {
                    logger.debug("About to look for case instance {} tasks with status {} assigned to business admin {}", caseId, status, user);
                    TaskSummaryList responseObject = this.caseManagementRuntimeDataServiceBase.getCaseTasksAsBusinessAdmin(caseId, user, status, page, pageSize, sort, sortOrder);

                    logger.debug("Returning OK response with content '{}'", responseObject);
                    return createCorrectVariant(responseObject, headers, Response.Status.OK, customHeaders);
                });
    }

    @GET
    @Path(CASE_TASKS_AS_STAKEHOLDER_GET_URI)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCaseInstanceTasksAsStakeholder(@javax.ws.rs.core.Context HttpHeaders headers,
            @PathParam(CASE_ID) String caseId,
            @QueryParam("user") String user, @QueryParam("status") List<String> status,
            @QueryParam("page") @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
            @QueryParam("sort") String sort, @QueryParam("sortOrder") @DefaultValue("true") boolean sortOrder) {

        return invokeCaseOperation(headers,
                "",
                null,
                (Variant v, String type, Header... customHeaders) -> {
                    logger.debug("About to look for case instance {} tasks with status {} assigned to stakeholder {}", caseId, status, user);
                    TaskSummaryList responseObject = this.caseManagementRuntimeDataServiceBase.getCaseTasksAsStakeholder(caseId, user, status, page, pageSize, sort, sortOrder);

                    logger.debug("Returning OK response with content '{}'", responseObject);
                    return createCorrectVariant(responseObject, headers, Response.Status.OK, customHeaders);
                });
    }

    @GET
    @Path(CASE_FILE_GET_URI)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCaseInstanceDataItems(@javax.ws.rs.core.Context HttpHeaders headers, @PathParam(CASE_ID) String caseId,
            @QueryParam("name") List<String> names, @QueryParam("type") List<String> types,
            @QueryParam("page") @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("10") Integer pageSize) {
        return invokeCaseOperation(headers,
                "",
                caseId,
                (Variant v, String type, Header... customHeaders) -> {
                    logger.debug("About to load case file data items of case {}", caseId);
                    CaseFileDataItemList response = this.caseManagementRuntimeDataServiceBase.getCaseInstanceDataItems(caseId, names, types, page, pageSize);

                    logger.debug("Returning OK response with content '{}'", response);
                    return createCorrectVariant(response, headers, Response.Status.OK, customHeaders);
                });
    }
}