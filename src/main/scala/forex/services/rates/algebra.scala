package forex.services.rates

import forex.domain.Rate
import forex.domain.Rate.Pair
import forex.services.rates.errors._

trait Algebra[F[_]] {
  def getRates(pairs: List[Pair]): F[Either[Error, Map[Rate.Pair, Rate]]]
}
