package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.test.Helpers.{await => pawait}
import play.api.libs.ws.WS

class IntegrationSpec extends Specification {

  "Application" should {

    "compute localized messages" in {
      running(TestServer(3333), HTMLUNIT) { browser =>

        val testsEn = List(
          "Hello World!",
          "Hello People!",
          "Complex key",
          "Arg0 Zero and arg1 1 are on a boat",
          "wrong.key",
          "I'm happy!",
          "Hi there World!",
          "Now valid",
          "Greetings baby!",
          "Zero, 1111 and true"
        ).zipWithIndex

        val testsEnUS = List(
          "Hello US World!",
          "Hello US People!",
          "Complex key",
          "Arg0 Zero and arg1 1 are on a boat",
          "wrong.key",
          "I'm happy!",
          "Hi there World US!",
          "Now valid US",
          "Greetings baby US!",
          "Zero, 1111 and true"
        ).zipWithIndex

        val testsFr = List(
          "Bonjour World !",
          "Bonjour People !",
          "Clé complexe",
          "Arg0 Zero and arg1 1 are on a boat",
          "wrong.key",
          "Salut l'ami",
          "Salut ici World!",
          "Désormais valide",
          "Salut toi!",
          "Zero, 1111 et true"
        ).zipWithIndex

        for (i <- 1 to 2) {
          browser.goTo("http://localhost:3333/test/" + i)
          val divs = browser.$("div")
          testsEnUS.foreach { kv =>
            divs.get(kv._2).getText must equalTo (kv._1)
          }

          browser.goTo("http://localhost:3333/all/" + i)
          val allDivs = browser.$("div")
          testsEn.foreach { kv =>
            allDivs.get(kv._2).getText must equalTo (kv._1)
          }
        }

        browser.goTo("http://localhost:3333/en")
        val allDivsEn = browser.$("div")
        testsEn.foreach { kv =>
          allDivsEn.get(kv._2).getText must equalTo (kv._1)
        }

        browser.goTo("http://localhost:3333/enUS")
        val allDivsEnUS = browser.$("div")
        testsEnUS.foreach { kv =>
          allDivsEnUS.get(kv._2).getText must equalTo (kv._1)
        }

        browser.goTo("http://localhost:3333/fr")
        val allDivsFr = browser.$("div")
        testsFr.foreach { kv =>
          allDivsFr.get(kv._2).getText must equalTo (kv._1)
        }

        browser.goTo("http://localhost:3333/cn")
        val allDivsCn = browser.$("div")
        testsEn.foreach { kv =>
          allDivsCn.get(kv._2).getText must equalTo (kv._1)
        }

        browser.goTo("http://localhost:3333/noLang")
        val allDivsNoLang = browser.$("div")
        testsEn.foreach { kv =>
          allDivsNoLang.get(kv._2).getText must equalTo (kv._1)
        }

        val testsSubset = List(
          "Hello US World!",
          "Hello US People!",
          "root.parent.child",
          "I'm happy!",
          "error.maxLength"
        ).zipWithIndex

        browser.goTo("http://localhost:3333/subset")
        val allDivsSubset = browser.$("div")
        testsSubset.foreach { kv =>
          allDivsSubset.get(kv._2).getText must equalTo (kv._1)
        }

        browser.goTo("http://localhost:3333/subsetAll")
        val allDivsSubsetAll = browser.$("div")
        testsSubset.foreach { kv =>
          allDivsSubsetAll.get(kv._2).getText must equalTo (kv._1)
        }

        val testsFilter = List(
          "Required field",
          "greeting",
          "wrong",
          "root.parent.child",
          "Field is way too long"
        ).zipWithIndex

        browser.goTo("http://localhost:3333/filter")
        val allDivsFilter = browser.$("div")
        testsFilter.foreach { kv =>
          allDivsFilter.get(kv._2).getText must equalTo (kv._1)
        }

        browser.goTo("http://localhost:3333/filterAll")
        val allDivsFilterAll = browser.$("div")
        testsFilter.foreach { kv =>
          allDivsFilterAll.get(kv._2).getText must equalTo (kv._1)
        }

        pawait(WS.url("http://localhost:3333/messages.js").withHeaders("Accept-Language"->"en").get()).body must contain ("Hello {0}!")
        pawait(WS.url("http://localhost:3333/messages.js").withHeaders("Accept-Language"->"fr").get()).body must contain ("Bonjour {0} !")
        pawait(WS.url("http://localhost:3333/all-messages.js").get()).body must contain ("Hello {0}!")
        pawait(WS.url("http://localhost:3333/all-messages.js").get()).body must contain ("Bonjour {0} !")
      }
    }
  }
}
