import random
def main():
    num_strands = 1000
    strand_length = 1000
    letters = 'atcg'
    randomDNA = []

    for i in xrange(0, num_strands):
        dna = ''
        for l in xrange(0, strand_length):
            c = letters[random.randint(0, 3)]
            dna+=(c)
        randomDNA.append(dna)
    s = ('\n'.join(randomDNA))
    print(s)
main()
