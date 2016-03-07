package io.getquill.sources.async.mysql

import io.getquill.sources.sql.ProductSpec

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }
import scala.concurrent.ExecutionContext.Implicits.global

import io.getquill._

class ProductAsyncSpec extends ProductSpec {

  def await[T](future: Future[T]) = Await.result(future, Duration.Inf)

  override def beforeAll = {
    await(testMysqlDB.run(quote(query[Product].delete)))
    ()
  }

  "Product" - {
    "Insert multiple products" in {
      val inserted = await(testMysqlDB.run(productInsert)(productEntries))
      val product = await(testMysqlDB.run(productById)(inserted(2))).head
      product.description mustEqual productEntries(2).description
      product.id mustEqual inserted(2)
    }
    "Single insert product" in {
      val inserted = await(testMysqlDB.run(productSingleInsert))
      val product = await(testMysqlDB.run(productById)(inserted)).head
      product.description mustEqual "Window"
      product.id mustEqual inserted
    }
  }

}
