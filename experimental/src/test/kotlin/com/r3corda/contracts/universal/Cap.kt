package com.r3corda.contracts.universal

import com.r3corda.core.contracts.BusinessCalendar
import com.r3corda.core.contracts.FixOf
import com.r3corda.core.contracts.Frequency
import com.r3corda.core.contracts.Tenor
import com.r3corda.core.utilities.DUMMY_NOTARY
import com.r3corda.testing.transaction
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

/**
 * Created by sofusmortensen on 05/09/16.
 */

class Cap {

    val TEST_TX_TIME_1: Instant get() = Instant.parse("2017-09-02T12:00:00.00Z")

    val notional = 50.M
    val currency = EUR

    val tradeDate: LocalDate = LocalDate.of(2016, 9, 1)

    val contract = arrange {
        rollOut("2016-09-01".ld, "2017-09-01".ld, Frequency.Quarterly) {
            actions {
                (acmeCorp or highStreetBank).may {
                    "exercise".anytime {
                        val floating = interest(notional, "act/365", fix("LIBOR", start, Tenor("3M")), start, end)
                        val fixed = interest(notional, "act/365", 0.5.bd, start, end)
                        highStreetBank.gives(acmeCorp, floating - fixed, currency)
                        next()
                    }
                }
                acmeCorp.may {
                    "skip".anytime {
                        next()
                    }
                }
            }
        }
    }

    val contractFixed = arrange {
        actions {
            (acmeCorp or highStreetBank).may {
                "exercise".anytime() {
                    val floating1 = interest(notional, "act/365", 1.0.bd, "2016-09-01", "2016-12-01")
                    val fixed1 = interest(notional, "act/365", 0.5.bd, "2016-09-01", "2016-12-01")
                    highStreetBank.gives(acmeCorp, floating1 - fixed1, currency)
                    rollOut("2016-12-01".ld, "2017-09-01".ld, Frequency.Quarterly) {
                        actions {
                            (acmeCorp or highStreetBank).may {
                                "exercise".anytime {
                                    val floating = interest(notional, "act/365", fix("LIBOR", start, Tenor("3M")), start, end)
                                    val fixed = interest(notional, "act/365", 0.5.bd, start, end)
                                    highStreetBank.gives(acmeCorp, floating - fixed, currency)
                                    next()
                                }
                            }
                            acmeCorp.may {
                                "skip".anytime {
                                    next()
                                }
                            }
                        }
                    }
                }
            }
            acmeCorp.may {
                "skip".anytime {
                    rollOut("2016-12-01".ld, "2017-09-01".ld, Frequency.Quarterly) {
                        actions {
                            (acmeCorp or highStreetBank).may {
                                "exercise".anytime {
                                    val floating = interest(notional, "act/365", fix("LIBOR", start, Tenor("3M")), start, end)
                                    val fixed = interest(notional, "act/365", 0.5.bd, start, end)
                                    highStreetBank.gives(acmeCorp, floating - fixed, currency)
                                    next()
                                }
                            }
                            acmeCorp.may {
                                "skip".anytime {
                                    next()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    val contractFixed2 = arrange {
        actions {
            (acmeCorp or highStreetBank).may {
                "exercise".anytime() {
                    val floating1 = interest(notional, "act/365", 1.0.bd, "2016-12-01", "2017-03-01")
                    val fixed1 = interest(notional, "act/365", 0.5.bd, "2016-12-01", "2017-03-01")
                    highStreetBank.gives(acmeCorp, floating1 - fixed1, currency)
                    rollOut("2017-03-01".ld, "2017-09-01".ld, Frequency.Quarterly) {
                        actions {
                            (acmeCorp or highStreetBank).may {
                                "exercise".anytime {
                                    val floating = interest(notional, "act/365", fix("LIBOR", start, Tenor("3M")), start, end)
                                    val fixed = interest(notional, "act/365", 0.5.bd, start, end)
                                    highStreetBank.gives(acmeCorp, floating - fixed, currency)
                                    next()
                                }
                            }
                            acmeCorp.may {
                                "skip".anytime {
                                    next()
                                }
                            }
                        }
                    }
                }
            }
            acmeCorp.may {
                "skip".anytime {
                    rollOut("2017-03-01".ld, "2017-09-01".ld, Frequency.Quarterly) {
                        actions {
                            (acmeCorp or highStreetBank).may {
                                "exercise".anytime {
                                    val floating = interest(notional, "act/365", fix("LIBOR", start, Tenor("3M")), start, end)
                                    val fixed = interest(notional, "act/365", 0.5.bd, start, end)
                                    highStreetBank.gives(acmeCorp, floating - fixed, currency)
                                    next()
                                }
                            }
                            acmeCorp.may {
                                "skip".anytime {
                                    next()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val contractAfterExecute = arrange {
        rollOut("2016-12-01".ld, "2017-09-01".ld, Frequency.Quarterly) {
            actions {
                (acmeCorp or highStreetBank).may {
                    "exercise".anytime {
                        val floating = interest(notional, "act/365", fix("LIBOR", start, Tenor("3M")), start, end)
                        val fixed = interest(notional, "act/365", 0.5.bd, start, end)
                        highStreetBank.gives(acmeCorp, floating - fixed, currency)
                        next()
                    }
                }
                acmeCorp.may {
                    "skip".anytime {
                        next()
                    }
                }
            }
        }
    }

    val paymentFirst = arrange { highStreetBank.gives(acmeCorp, 250.K, EUR) }


    val stateStart = UniversalContract.State(listOf(DUMMY_NOTARY.owningKey), contract)

    val stateFixed = UniversalContract.State( listOf(DUMMY_NOTARY.owningKey), contractFixed)

    val stateAfterExecute = UniversalContract.State( listOf(DUMMY_NOTARY.owningKey), contractAfterExecute)
    val statePaymentFirst = UniversalContract.State( listOf(DUMMY_NOTARY.owningKey), paymentFirst)

    val stateFixed2 = UniversalContract.State( listOf(DUMMY_NOTARY.owningKey), contractFixed2)

    val contractLimitedCap = arrange {
        rollOut("2016-04-01".ld, "2017-04-01".ld, Frequency.Quarterly, object {
            val limit = variable(150.K)
        }) {
            actions {
                (acmeCorp or highStreetBank).may {
                    "exercise".anytime {
                        val floating = interest(notional, "act/365", fix("LIBOR", start, Tenor("3M")), start, end)
                        val fixed = interest(notional, "act/365", 0.5.bd, start, end)
                        val payout = min(floating - fixed)
                        highStreetBank.gives(acmeCorp, payout, currency)
                        next(vars.limit to vars.limit - payout)
                    }
                }
                acmeCorp.may {
                    "skip".anytime {
                        next()
                    }
                }
            }
        }
    }

    @Test
    fun issue() {
        transaction {
            output { stateStart }
            timestamp(TEST_TX_TIME_1)

            this `fails with` "transaction has a single command"

            tweak {
                command(acmeCorp.owningKey) { UniversalContract.Commands.Issue() }
                this `fails with` "the transaction is signed by all liable parties"
            }

            command(highStreetBank.owningKey) { UniversalContract.Commands.Issue() }

            this.verifies()
        }
    }

    @Test
    fun `first fixing`() {
        transaction {
            input { stateStart }
            output { stateFixed }
            timestamp(TEST_TX_TIME_1)

            tweak {
                command(highStreetBank.owningKey) { UniversalContract.Commands.Action("some undefined name") }
                this `fails with` "action must be defined"
            }

            tweak {
                // wrong source
                command(highStreetBank.owningKey) { UniversalContract.Commands.Fix(listOf(com.r3corda.core.contracts.Fix(FixOf("LIBORx", tradeDate, Tenor("3M")), 1.0.bd))) }

                this `fails with` "relevant fixing must be included"
            }

            tweak {
                // wrong date
                command(highStreetBank.owningKey) { UniversalContract.Commands.Fix(listOf(com.r3corda.core.contracts.Fix(FixOf("LIBOR", tradeDate.plusYears(1), Tenor("3M")), 1.0.bd))) }

                this `fails with` "relevant fixing must be included"
            }

            tweak {
                // wrong tenor
                command(highStreetBank.owningKey) { UniversalContract.Commands.Fix(listOf(com.r3corda.core.contracts.Fix(FixOf("LIBOR", tradeDate, Tenor("9M")), 1.0.bd))) }

                this `fails with` "relevant fixing must be included"
            }

            tweak {
                command(highStreetBank.owningKey) { UniversalContract.Commands.Fix(listOf(com.r3corda.core.contracts.Fix(FixOf("LIBOR", tradeDate, Tenor("3M")), 1.5.bd))) }

                this `fails with` "output state does not reflect fix command"
            }

            command(highStreetBank.owningKey) { UniversalContract.Commands.Fix(listOf(com.r3corda.core.contracts.Fix(FixOf("LIBOR", tradeDate, Tenor("3M")), 1.0.bd))) }

            this.verifies()
        }
    }

    @Test
    fun `first execute`() {
        transaction {
            input { stateFixed }
            output { stateAfterExecute }
            output { statePaymentFirst }

            timestamp(TEST_TX_TIME_1)

            tweak {
                command(highStreetBank.owningKey) { UniversalContract.Commands.Action("some undefined name") }
                this `fails with` "action must be defined"
            }

            command(highStreetBank.owningKey) { UniversalContract.Commands.Action("exercise") }

            this.verifies()
        }
    }

    @Test
    fun `second fixing`() {
        transaction {
            input { stateAfterExecute }
            output { stateFixed2 }
            timestamp(TEST_TX_TIME_1)

            tweak {
                command(highStreetBank.owningKey) { UniversalContract.Commands.Action("some undefined name") }
                this `fails with` "action must be defined"
            }

            tweak {
                // wrong source
                command(highStreetBank.owningKey) { UniversalContract.Commands.Fix(listOf(com.r3corda.core.contracts.Fix(FixOf("LIBORx", BusinessCalendar.parseDateFromString("2016-12-01"), Tenor("3M")), 1.0.bd))) }

                this `fails with` "relevant fixing must be included"
            }

            tweak {
                // wrong date
                command(highStreetBank.owningKey) { UniversalContract.Commands.Fix(listOf(com.r3corda.core.contracts.Fix(FixOf("LIBOR", BusinessCalendar.parseDateFromString("2016-12-01").plusYears(1), Tenor("3M")), 1.0.bd))) }

                this `fails with` "relevant fixing must be included"
            }

            tweak {
                // wrong tenor
                command(highStreetBank.owningKey) { UniversalContract.Commands.Fix(listOf(com.r3corda.core.contracts.Fix(FixOf("LIBOR", BusinessCalendar.parseDateFromString("2016-12-01"), Tenor("9M")), 1.0.bd))) }

                this `fails with` "relevant fixing must be included"
            }

            tweak {
                command(highStreetBank.owningKey) { UniversalContract.Commands.Fix(listOf(com.r3corda.core.contracts.Fix(FixOf("LIBOR", BusinessCalendar.parseDateFromString("2016-12-01"), Tenor("3M")), 1.5.bd))) }

                this `fails with` "output state does not reflect fix command"
            }

            command(highStreetBank.owningKey) { UniversalContract.Commands.Fix(listOf(com.r3corda.core.contracts.Fix(FixOf("LIBOR", BusinessCalendar.parseDateFromString("2016-12-01"), Tenor("3M")), 1.0.bd))) }

            this.verifies()
        }
    }
}