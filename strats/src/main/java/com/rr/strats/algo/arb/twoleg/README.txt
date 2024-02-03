overview of the calendar arb strategy

On futures exchanges there are products called calendar spreads - this is basically an instrument that represents selling one maturity of a futures contract and 
buying the other maturity e.g. sell may buy june.

The price of a calendar spread is represented as the difference of the two outright legs 
i,e, the front month (the closer of the 2 expiries) and the back month (the later of the 2 expiries). For some products it's (front month-back month) and for other products its (back month - front month). Because calendar spreads are matched as an individual product in the matching engine, there are arbitrage opportunities that result from a synthetic calendar spread (explicitly buying or selling the individual legs) being over or undervalued compared to the actual calendar spread product. If the difference between the synthetic calendar spread and the exchange's calendar spread product is greater than a threshold that considers cost of execution, then we have a relatively risk free trade on our hands.

to buy a synthetic calendar spread, our buy price is going to be:

    Leg1Ask - Leg2Bid

to sell a synthetic calendar spread, our sell price is going to be:

    Leg1Bid - Leg2Ask

compare the buying or selling price of this synthetic to the actual price of the calendar spread. 
If the synthetic buy price is less than the bid price of the native exchange product price, 
it means we can buy the synthetic and sell the exchange calendar spread for a profit. 
The difference again needs to be above a threshold. 

If the synthetic sell price is greater than the ask price of the native exchange product, 
it means we can sell the synthetic and buy the exchange calendar spread for a profit. 
The difference again needs to be above a threshold.

There are basically going to be three orders we send out. Orders should be sent out ideally in order of least to most volume. 
There are a limited number of cases to cover in terms of executions coming back, 
but ideally we will get filled first time on all legs. 
We should initially send out IOCs on the three legs and then work the positions if necessary.

