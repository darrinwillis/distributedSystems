import random
def main():
    num_each = 1000000
    range = 1000
    random_nums = []

    for i in xrange(0, num_each):
        x = random.randint(-range, range)
        y = random.randint(-range, range)
        random_nums.append([x,y])
    f = open('random.txt', 'w')
    s = ('\n'.join('{0} {1}'.format(x, y) for (x,y) in random_nums))
    f.write(s)
main()
