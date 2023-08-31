package calebxzhou.craftcone.util

import org.bson.Document

/**
 * Created  on 2023-08-31,22:01.
 */

fun bsonOf(json: String) = json.replace('%','$').run { Document.parse(this) }