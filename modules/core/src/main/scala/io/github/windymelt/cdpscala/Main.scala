/*
 * Copyright (c) 2024 cdp-scala authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.windymelt.cdpscala

import TabSession.*
import cats.effect.IO
import cats.effect.IOApp

object Main extends IOApp.Simple {
  def run: IO[Unit] = for {
    _ <- IO.delay(println(msg))
    _ <- ChromeProcess.spawn().use { cp =>
      cp.newTabAutoClose().use { ts =>
        for
          _ <- IO.println("new tab opened")
          wsSession <- TabSession.openWsSession(ts)
          shot <- wsSession.use { s =>
            cmd.Page.navigate(s, "https://example.com/") >> cmd.Page
              .captureScreenshot(s, "png")
          }
          _ <- IO.delay {
            os.write.over(os.pwd / "ss.png.base64", shot)
          }
        yield ()
      }
    }
  } yield ()
}

def msg = "Chrome DevTools Protocol wrapper for Scala test command"
