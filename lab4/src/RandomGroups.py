import random
def main():
    num_each = 100
    centers = [[50,50], [-50, 50], [-50, -50], [50, -50]]
    range = 5
    random_nums = []

    for pair in centers:
        for i in xrange(0, num_each):
            x = random.randint(pair[0]-range, pair[0]+range)
            y = random.randint(pair[1]-range, pair[1]+range)
            random_nums.append([x,y])
    f = open('random.txt', 'w')
    s = ('\n'.join('{0} {1}'.format(x, y) for (x,y) in random_nums))
    f.write(s)
main()
