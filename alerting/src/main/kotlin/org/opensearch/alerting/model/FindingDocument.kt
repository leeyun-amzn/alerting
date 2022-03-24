package org.opensearch.alerting.model

import org.apache.logging.log4j.LogManager
import org.opensearch.common.io.stream.StreamInput
import org.opensearch.common.io.stream.StreamOutput
import org.opensearch.common.io.stream.Writeable
import org.opensearch.common.xcontent.ToXContent
import org.opensearch.common.xcontent.XContentBuilder
import org.opensearch.common.xcontent.XContentParser
import org.opensearch.common.xcontent.XContentParserUtils
import java.io.IOException

private val log = LogManager.getLogger(FindingDocument::class.java)

class FindingDocument(
    val index: String,
    val id: String,
    val version: Int?,
    val seqNo: Int?,
    val primaryTerm: Int?,
    val found: Boolean,
    val document: Map<String, Any>
) : Writeable, ToXContent {

    @Throws(IOException::class)
    constructor(sin: StreamInput) : this(
        index = sin.readString(),
        id = sin.readString(),
        version = sin.readInt(),
        seqNo = sin.readInt(),
        primaryTerm = sin.readInt(),
        found = sin.readBoolean(),
        document = sin.readMap()
    )

    override fun toXContent(builder: XContentBuilder, params: ToXContent.Params): XContentBuilder {
        builder.startObject()
            .field(INDEX_FIELD, index)
            .field(FINDING_DOCUMENT_ID_FIELD, id)

        if (found) {
            builder.field(VERSION_FIELD, version)
                .field(SEQ_NO_FIELD, seqNo)
                .field(PRIMARY_TERM_FIELD, primaryTerm)
        }

        builder.field(FOUND_FIELD, found)

        if (document.isEmpty()) builder.field(DOCUMENT_FIELD, document)
        builder.endObject()
        return builder
    }

    @Throws(IOException::class)
    override fun writeTo(out: StreamOutput) {
        out.writeString(index)
        out.writeString(id)
        out.writeOptionalInt(version)
        out.writeOptionalInt(seqNo)
        out.writeOptionalInt(primaryTerm)
        out.writeBoolean(found)
        out.writeMap(document)
    }

    companion object {
        const val INDEX_FIELD = "index"
        const val FINDING_DOCUMENT_ID_FIELD = "id"
        const val VERSION_FIELD = "version"
        const val SEQ_NO_FIELD = "seq_no"
        const val PRIMARY_TERM_FIELD = "primary_term"
        const val FOUND_FIELD = "found"
        const val DOCUMENT_FIELD = "document"
        const val NO_ID = ""

        @JvmStatic @JvmOverloads
        @Throws(IOException::class)
        fun parse(xcp: XContentParser, id: String = NO_ID): FindingDocument {
            lateinit var index: String
            lateinit var id: String
            var version: Int? = null
            var seqNo: Int? = null
            var primaryTerm: Int? = null
            var found = false
            var document: Map<String, Any> = mapOf()

            XContentParserUtils.ensureExpectedToken(XContentParser.Token.START_OBJECT, xcp.currentToken(), xcp)
            while (xcp.nextToken() != XContentParser.Token.END_OBJECT) {
                val fieldName = xcp.currentName()
                xcp.nextToken()
                // TODO: Remove debug log
                log.info("fieldName: $fieldName")

                when (fieldName) {
                    INDEX_FIELD -> index = xcp.text()
                    FINDING_DOCUMENT_ID_FIELD -> id = xcp.text()
                    VERSION_FIELD -> version = xcp.intValue()
                    SEQ_NO_FIELD -> seqNo = xcp.intValue()
                    PRIMARY_TERM_FIELD -> primaryTerm = xcp.intValue()
                    FOUND_FIELD -> found = xcp.booleanValue()
                    DOCUMENT_FIELD -> document = xcp.map()
                }
            }

            return FindingDocument(
                index = index,
                id = id,
                version = version,
                seqNo = seqNo,
                primaryTerm = primaryTerm,
                found = found,
                document = document
            )
        }

        @JvmStatic
        @Throws(IOException::class)
        fun readFrom(sin: StreamInput): FindingDocument {
            return FindingDocument(sin)
        }
    }
}
