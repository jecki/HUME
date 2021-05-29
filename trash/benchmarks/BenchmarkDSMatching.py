"""Benchmark for double sided matching. STATUS: very provisional...
"""

import random
from hume import PartitionNetwork, DoubleSidedMatching, TrustGame, Agent
from PyPlotter import Graph, Gfx

matching = DoubleSidedMatching()
network = PartitionNetwork(437, 17)
game = TrustGame()

for agent in network.arrayView():
    for i in range(Agent.numCompetences):
        agent.competences[i] = random.random()
    agent.normalizeCompetences()

#matching.matchAgents(network, game)

#print matching.ac.turnBacks
#print matching.ac.lastResorts

gr = Graph.Cartesian(None, 0.0, 0.0, 1.0, 1.0, 
                     "Benchmark of Double Sided Matching Algorithm",
                     "discount ratio","bad matches")
gr2 = Graph.Cartesian(Gfx.nilDriver(), 0.0, -0.5, 1.0, 0.0,
                      "Benchmark of Double Sided Matching Algorithm",
                      "dicsount ratio", "expectation deviations")

for i in range(11):
    discount = float(i) / 10.0
    matching.serviceDiscount = 0.3
    matching.earningsDiscount = discount
    matches = matching.matchAgents(network, game)
    gr.addValue("turn backs", discount, matching.ac.turnBacks/437.0)
    gr.addValue("last resorts", discount, matching.ac.lastResorts/437.0)
    
    count = 0; service = 0.0; earnings = 0.0 
    estService = 0.0; estEarnings = 0.0
    for a1, a2 in matches:
        if a1 != a2:
            if a2.exploit(a1):
                service += game.customersExploit(a2.competence(a1.currentProblem))
                earnings += game.suppliersExploit(a2.competence(a1.currentProblem))
            else:
                service += game.customersReward(a2.competence(a1.currentProblem))
                earnings += game.suppliersReward(a2.competence(a1.currentProblem))
            estService += matching.ac.estimates[a1][0]
            estEarnings += matching.ac.estimates[a2][1]
            count += 1
    deltaService = (service/count - estService/count)
    deltaEarnings = (earnings/count - estEarnings/count)
    #gr2.addValue("service deviation", discount, deltaService)
    gr2.addValue("earnings deviation", discount, deltaEarnings)
    
    gr.dumpPostscript("BenchmarkDSM_Matches_3.eps")
    gr2.dumpPostscript("BenchmarkDSM_Expectations_3.eps")



#    first try...
#
#    count = 0; sd = 0; ed = 0
#    for a1, a2 in matches:
#        if a1 != a2:
#            if a2.exploit(a1):
#                service = game.customersExploit(a2.competence(a1.currentProblem))
#                earnings = game.suppliersExploit(a2.competence(a1.currentProblem))
#            else:
#                service = game.customersReward(a2.competence(a1.currentProblem))
#                earnings = game.suppliersReward(a2.competence(a1.currentProblem))
#            sd += abs(service - matching.ac.estimates[a1][0])
#            ed += abs(earnings - matching.ac.estimates[a2][1])
#            count += 1
#    gr.addValue("service deviation", discount, sd/count)
#    gr.addValue("earnings deviation", discount, ed/count)
    
        