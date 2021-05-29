from pylab import *

def costs(x):
    return 1.0-(x**(1.0/4.0)*0.8)

def value(x):
    return x**(1.0/3.0)

def price(x):
    return value(x)*0.5+0.2
    # return array([0.5 for xval in x])

def breakEven(x):
    return array([0.0 for xval in x])


x = arange(0.0, 1.0, 0.001)

subplot(311)
plot(x, costs(x), x, value(x), x, price(x), "--")
legend(("costs", "value", "price"), loc="lower right")
xlabel("competence")
ylabel("money units")

subplot(312)
plot(x, price(x)-costs(x), x, price(x), x, breakEven(x), ":")
legend(("rewarder", "cheater", "break even"), loc="lower right")
xlabel("competence")
ylabel("sagent payoff")

subplot(313)
plot(x, value(x)-price(x), x, -price(x), x, breakEven(x), ":")
legend(("rewarded", "cheated", "break even"), loc="lower right")
xlabel("competence")
ylabel("pagent payoff")

show()



