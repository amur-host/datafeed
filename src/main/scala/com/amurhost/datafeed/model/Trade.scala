package com.amurhost.datafeed.model

import com.amurhost.datafeed.utils.AmurAddress
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Trade(amountAsset: String, priceAsset: String, timestamp: Long, id: String, orderType: Byte, price: Long, amount: Long, buyer: String, seller: String, matcher: String) {

  def toJson(confirmed: Boolean): JsObject =
    Json.obj(
      "timestamp" -> timestamp,
      "id" -> id,
      "confirmed" -> confirmed,
      "type" -> (if (orderType == 0) "buy" else "sell"),
      "price" -> price,
      "amount" -> amount,
      "buyer" -> buyer,
      "seller" -> seller,
      "matcher" -> matcher
    )

}

object Trade {

  private def setTrade(amountAsset: Option[String], priceAsset: Option[String], timestamp: Long, id: String, order1ts: Long, order2ts: Long, price: Long, amount: Long, buyer: String, seller: String, matcher: String): Trade =
    apply(amountAsset.getOrElse("AMUR"), priceAsset.getOrElse("AMUR"), timestamp, id, (if(order1ts > order2ts) 0.toByte else 1.toByte), price, amount, AmurAddress.fromPublicKey(buyer), AmurAddress.fromPublicKey(seller), matcher)

    implicit val readTrade: Reads[Trade] = (
    (JsPath \ "order1" \ "assetPair" \ "amountAsset").readNullable[String] and
    (JsPath \ "order1" \ "assetPair" \ "priceAsset").readNullable[String] and
    (JsPath \ "timestamp").read[Long] and
    (JsPath \ "id").read[String] and
    (JsPath \ "order1" \ "timestamp").read[Long] and
    (JsPath \ "order2" \ "timestamp").read[Long] and
    (JsPath \ "price").read[Long] and
    (JsPath \ "amount").read[Long] and
    (JsPath \ "order1" \ "senderPublicKey").read[String] and
    (JsPath \ "order2" \ "senderPublicKey").read[String] and
    (JsPath \ "order1" \ "matcherPublicKey").read[String]
  ) (Trade.setTrade _)

}