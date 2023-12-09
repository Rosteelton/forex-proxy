package forex

import forex.domain.Rate
import forex.domain.Rate.Pair

package object services {
  type RatesService[F[_]] = rates.Algebra[F]
  final val RatesServices = rates.Interpreters

  type RatesCacheService[F[_]] = cache.Algebra[F, Pair, Rate]
  final val RatesCacheService = cache.Interpreters
}
