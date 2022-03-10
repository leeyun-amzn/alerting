/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.alerting.resthandler

/**
 * This class consists of the REST handler to search findings .
 */
class RestGetFindingsSearchAction : BaseRestHandler() {

    private val log = LogManager.getLogger(RestGetFindingsSearchAction::class.java)

    override fun getName(): String {
        return "get_findings_search_action"
    }

    override fun routes(): List<Route> {
        return listOf()
    }

    override fun prepareRequest(request: RestRequest, client: NodeClient): RestChannelConsumer {
        log.debug("${request.method()} ${request.path()}")

        val findingID: String? = request.param("findingID")

        var srcContext = context(request)
        if (request.method() == RestRequest.Method.HEAD) {
            srcContext = FetchSourceContext.DO_NOT_FETCH_SOURCE
        }

        val sortString = request.param("sortString", "destination.name.keyword")
        val sortOrder = request.param("sortOrder", "asc")
        val missing: String? = request.param("missing")
        val size = request.paramAsInt("size", 20)
        val startIndex = request.paramAsInt("startIndex", 0)
        val searchString = request.param("searchString", "")

        val table = Table(
            sortOrder,
            sortString,
            missing,
            size,
            startIndex,
            searchString
        )

        val getFindingsSearchRequest = GetFindingsSearchRequest(
            findingID,
            RestActions.parseVersion(request),
            srcContext,
            table
        )
        return RestChannelConsumer {
            channel ->
            client.execute(RestGetFindingsSearchAction.INSTANCE, getFindingsSearchRequest, RestToXContentListener(channel))
        }
    }
}
