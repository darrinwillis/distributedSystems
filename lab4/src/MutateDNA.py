import random
def main():
    num_seeds = 5;
    num_strands = 1000
    avg_mutations = 5
    mutation_var = 1
    mutation_length = 200
    strand_length = 1000
    letters = 'atcg'
    randomDNAs = []
    seeds = []

    for i in xrange(0, num_seeds):
        dna = []
        for l in xrange(0, strand_length):
            c = letters[random.randint(0, 3)]
            dna.append(c)
        seeds.append(dna)

    for i in xrange(0, num_strands):
        base = seeds[random.randint(0, num_seeds-1)]
        num_muts = avg_mutations + random.randint(-mutation_var, mutation_var)
        for l in xrange(0, num_muts):
            mutstart = random.randint(0, strand_length-mutation_length)
            for t in xrange(0, mutation_length):
                base[mutstart+t] = letters[random.randint(0,3)]
        randomDNAs.append(base)
    s = ('\n'.join("".join(k) for k in randomDNAs))
    print(s)
main()
