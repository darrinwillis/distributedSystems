import random
def main():
    num_each = 20
    range = 10
    random_nums = []

    for i in xrange(0, num_each):
        x = random.randint(-range, range)
        y = random.randint(-range, range)
        random_nums.append([x,y])
    s = ('\n'.join('{0} {1}'.format(x, y) for (x,y) in random_nums))
    print(s)
main()
