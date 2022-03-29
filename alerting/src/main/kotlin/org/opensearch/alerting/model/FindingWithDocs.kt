package org.opensearch.alerting.model

import org.apache.logging.log4j.LogManager
import org.opensearch.common.io.stream.StreamInput
import org.opensearch.common.io.stream.StreamOutput
import org.opensearch.common.io.stream.Writeable
import java.io.IOException

private val log = LogManager.getLogger(Finding::class.java)

class FindingWithDocs(
    val finding: Finding,
    val documents: List<FindingDocument>
) : Writeable {

    @Throws(IOException::class)
    constructor(sin: StreamInput) : this(
        finding = Finding.readFrom(sin),
        documents = sin.readList((FindingDocument)::readFrom)
    )

    @Throws(IOException::class)
    override fun writeTo(out: StreamOutput) {
        finding.writeTo(out)
        documents.forEach {
            it.writeTo(out)
        }
    }

    companion object {
        @JvmStatic
        @Throws(IOException::class)
        fun readFrom(sin: StreamInput): FindingWithDocs {
            return FindingWithDocs(sin)
        }
    }
}
