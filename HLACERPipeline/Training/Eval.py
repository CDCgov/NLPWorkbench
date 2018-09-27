import sys
import os
from optparse import OptionParser

"""
This is the file to compute the evaluation statistics.
"""

class Instance:

    def __init__(self, start, end, tag, text):
        if start < 0 or end < 0:
            raise ValueError, "positions must be non-negative"
        if start > end:
            raise ValueError, "start must not be after end"
        self.__start = start
        self.__end = end
        self.__tag = tag
        self.__text = text
        
    @apply
    def start():
        def fget(self):
            return self.__start
        doc = "start of the instance"
        return property(**locals())
	    
    @apply
    def end():
        def fget(self):
            return self.__end
        doc = "end of the instance"
        return property(**locals())

    @apply
    def text():
        def fget(self):
            return self.__text
        doc = "text of the instance"
        return property(**locals())

    @apply
    def tag():
        def fget(self):
	        return self.__tag
        doc = "tag of the instance"
        return property(**locals())

    def __len__(self):
        return self.__end - self.__start
        
    def __lt__(self, other):
        return self.__end <= other.start
    
    def __eq__(self, other):
        return self.__start == other.start and self.__end == other.end
    
    def __gt__(self, other):
        return other < self
        
    def __str__(self):
        return str((self.__start, self.__end, self.__tag, self.__text))
        
    def __repr__(self):
        return "Instance of %s at (%s, %s): %s" % (self.__tag, self.__start, self.__end,  self.text)

    def __hash__(self):
        return hash((self.__start, self.__end, self.__tag))
        
    def equals(self, other, ignoreTag=False):
        fp = self == other 
        if ignoreTag:
            return fp
        else:
            return (self.__tag == other.tag) and fp
        
    def left(self, other, ignoreTag=False):

        fp = self.__start == other.start and self.__end!=other.end
        
        if ignoreTag:
            return fp
        else:
            return self.__tag == other.tag and fp
        
    def overlapping(self, other, ignoreTag=False):
    
        fp = self.__end > other.start and self.__start<other.end
        
        if ignoreTag:
            return fp
        else:
            return self.__tag == other.tag and fp


    def right(self, other, ignoreTag=False):

        fp = (self.__end == other.end) and (self.__start != other.start)
        if ignoreTag:
            return fp
        else:
            return (self.__tag == other.tag) and fp
    
    def meets(self, other, ignoreTag=False):

        fp = (self.__start < other.start and self.__end > other.start and self.__end < other.end) or (self.__end > other.end and self.__start < other.end and self.__start >other.start)
        #fp = (self.__start < other.start and self.__end > other.start and self.__end <= other.end) or (self.__end > other.end and self.__start < other.end and self.__start >=other.start)

        if ignoreTag:
            return fp
        else:
            return (self.__tag == other.tag) and fp
        
    def contains(self, other, ignoreTag=False):
        """
        return True if self contains other
        """
        fp = (self.__start < other.start and self.__end > other.end) or (self.__start < other.start and self.__end > other.end)
        if ignoreTag:
            return fp
        else:
            return (self.__tag == other.tag) and fp

    def coveredby(self, other, ignoreTag=False):
        """
        return True if self is covered by other
        """
        fp = (self.__start >other.start and self.__end < other.end) or (self.__start > other.start and self.__end < other.end)

        #fp = (self.__start >=other.start and self.__end < other.end) or (self.__start > other.start and self.__end <= other.end)

        if ignoreTag:
            return fp
        else:
            return (self.__tag == other.tag) and fp
    
    def overlaps(self, other, ignoreTag=False):
        #fp = self.meets(other) or self.contains(other) or self.coveredby(other) or self.left(other) or self.right(other)

        fp = self.left(other) or self.right(other)

        if ignoreTag:
            return fp
        else:
            return (self.__tag == other.tag) and fp

    def excludes(self, other, ignoreTag=False):
       return self.__end <= other.start or self.__start >= other.end
    
    
class Sentence:
    def __init__(self, sentence):
        self.__tokens, self.__tags = zip(*sentence)
        
    def getTokens(self):
        return self.tokens
    
    def __str__(self):
        return " ".join(self.tokens)
    
    def __eq__(self, other):
        if len(self.__tokens)!=len(other.tokens):
            return False
        for (st, ot) in zip(self.__tags, other.tags):
            if st!=ot:
                return False
        for (st, ot) in zip(self.__tokens, other.tokens):
            if st!=ot:
                return False
        return True
                
    @apply
    def tags():
        def fget(self):
	        return self.__tags
        doc = "tags of the sentence"
        return property(**locals())
        
    @apply
    def tokens():
        def fget(self):
	        return self.__tokens
        doc = "tags of the sentence"
        return property(**locals())
        
    def getMissingInstances(self, other):
        """
        Return instances in other but not in self
        """
        #if self != other:
        #    raise TypeError, "Must be the same sentence"
        missing = []
        for o in other.getInstances():
            overlap = False
            for s in self.getInstances():
                if not s.excludes(o):
                    overlap = True
                    break
            if not overlap:
                missing.append(o)
        return missing
        
    def getGSMissingInstances(self, other):
        missing = []
        for s in self.getInstances():
            overlap = False
            for o in other.getInstances():
                if not s.excludes(o):
                    overlap = True
                    break
            if not overlap:
                missing.append(s)
        return missing

    def getExactInstances(self, other):
        instance = self.getInstances()
        otherInstances = other.getInstances()
        sinsts = set(instance)
        oinsts = set(otherInstances)
        sintersection = sinsts.intersection(oinsts)
        return list(sintersection)
        
    def getDifferentInstances(self, other):
        """
        Return different instances between self and other.
        """
        instances = self.getInstances()
        otherInstances = other.getInstances()
        sinsts = set(instances)
        oinsts = set(otherInstances)
        sdiff = list(sinsts - oinsts)
        odiff = list(oinsts - sinsts)
        return sdiff
        #return (sdiff, odiff)

    def getLeftBoundaryInstances(self, other):
        """
        
        """
        res = []
        sinstances = self.getInstances()
        oinstances = other.getInstances()
        for s in sinstances:
            for o in oinstances:
                if s.left(o) or s.equals(o):
                    res.append(s)

                    break
        #print len(res)
        return res

    def getRightBoundaryInstances(self, other):
        res = []
        sinstances = self.getInstances()
        oinstances = other.getInstances()
        for s in sinstances:
            for o in oinstances:
                if s.right(o) or s.equals(o):
                    res.append(s)
                    break
        return res

    def getEitherBoundaryInstances(self, other):
        res = []
        sinstances = self.getInstances()
        oinstances = other.getInstances()
        for s in sinstances:
            for o in oinstances:
                if s.right(o) or s.left(o) or s.equals(o):
                    res.append(s)

                    break
        return res
    
    def getOverlapInstances(self, other):
        res = []
        sinstances = self.getInstances()
        oinstances = other.getInstances()
        for s in sinstances:
            for o in oinstances:
                if s.overlapping(o, True):
                #if s.meets(o) or s.left(o) or s.equals(o) or s.right(o) or s.overlaps(o) or s.contains(o) or s.coveredby(o):
                    res.append(s)
                    break
        return res
        
    def getCoveredInstances(self, other):
        res = []
        sinstances = self.getInstances()
        oinstances = other.getInstances()
        for s in sinstances:
            for o in oinstances:
                if s.coveredby(o):
                    res.append(s)
                    break
        return res

    def getContainsInstances(self, other):
        res = []
        sinstances = self.getInstances()
        oinstances = other.getInstances()
        for s in sinstances:
            for o in oinstances:
                if s.contains(o):
                    res.append(s)
                    break
        return res

    def getInstances(self):
        """
        Return entity extents, in (start, end, tagtype) token positions
        """
        extents = []
        currentTagType = None
        extentStart = 0
        for i, token in enumerate(self.tokens):
            tag = self.__tags[i]
            if tag[0] == "B":
                if currentTagType:
                    instance = Instance(extentStart, i, currentTagType, " ".join(self.__tokens[extentStart:i]))
                    extents.append(instance)
                currentTagType = tag[2:]
                extentStart = i
            elif tag[0] == "I":
                pass
            elif tag[0] == "O":
                if currentTagType:
                    instance = Instance(extentStart, i, currentTagType, " ".join(self.__tokens[extentStart:i]))
                    extents.append(instance)
                    currentTagType = None
        if currentTagType:
            instance = Instance(extentStart, i+1, currentTagType, " ".join(self.__tokens[extentStart:i+1]))
            extents.append(instance)
        return extents
        
class BIOFileReader:
    
    def __init__(self, filename, column):
        self.__sentences = None
        self.__loadBIOFile(filename, column)
        self.index = 0
        
    def __loadBIOFile(self, filename, column):
        sents = []
        sentence = []
        for line in file(filename):
            if not line.strip():
                if sentence:
                    sent = Sentence(sentence)
                    sents.append(sent)
                    sentence = []
                continue
            row = line.split()
            token = row[0]  
            tag = row[column]
            sentence.append((token, tag))
        self.__sentences = sents

    def __len__(self):
        return len(self.__sentences)
        
    def __iter__(self):
        return self
        
    def next(self):
        if self.index == len(self.__sentences):
            raise StopIteration
        self.index = self.index + 1
        return self.__sentences[self.index-1]

    def getAnnotatedSentences(self):
        """
        Return sentences that have at least one tag instance
        """
        for sentence in self.__sentences:
            extents = sentence.getExtents()
            if extents:
                yield sentence
                
    def getUnannotatedSentences(self):
        """
        Return sentences that have no tag instance
        """
        for sentence in self.__sentences:
            extents = sentence.getExtents()
            if not extents:
                yield sentence

    def getInstanceByTag(self, tag):
        instances = []
        

    @apply
    def sentences():
        def fget(self):
            return self.__sentences
        doc = "sentences in the file"
        return property(**locals())

    @apply
    def length():
        def fget(self):
            return len(self.__sentences)
        doc = "number of sentence in the file"
        return property(**locals())

def testInstance(instance, other):
    print instance, other
    print "overlaps:", instance.overlaps(other)
    print "contains:", instance.contains(other)
    print "meets:", instance.meets(other)
    print "left boundary:",instance.left(other)
    print "right boundary:", instance.right(other)
    print "equals:", instance.equals(other)
    print "coveredby:", instance.coveredby(other)
    print

def test():
    testInstance(Instance(5, 8, "A", "TEXT"), Instance(7, 10, "A", "TEXT"))
    testInstance(Instance(0, 8, "A", "TEXT"), Instance(3, 7, "A", "TEXT"))
    testInstance(Instance(3, 7, "A", "TEXT"), Instance(0, 8, "A", "TEXT"))
    testInstance(Instance(3, 7, "A", "TEXT"), Instance(3, 7, "A", "TEXT"))

    testInstance(Instance(3, 7, "A", "TEXT"), Instance(1, 7, "A", "TEXT"))
    testInstance(Instance(3, 7, "A", "TEXT"), Instance(4, 7, "A", "TEXT"))

    testInstance(Instance(3, 7, "A", "TEXT"), Instance(3, 6, "A", "TEXT"))
    testInstance(Instance(3, 7, "A", "TEXT"), Instance(3, 8, "A", "TEXT"))


class BIOAnalyser:
    def __init__(self, filename):
        self.bpredict = BIOFileReader(filename, -1)
        self.bgold = BIOFileReader(filename, -2)
        self.__tagset = self.__get_tagset()

    @apply
    def tagset():
        def fget(self):
            return self.__tagset
        return property(**locals())

    def __getSame(s, o):
        same = []
        for x in s:
            for y in o:
                if x.equals(y):
                    same.append(x)
        return same

    def __get_tagset(self):
        tagset = {}
        for i, gold in enumerate(self.bgold.sentences):
            for instance in gold.getInstances():
                tagset[instance.tag] = None
        return tagset.keys()

    def showMissingInstances(self):
        for i, gold in enumerate(self.bgold.sentences):
            predict = self.bpredict.sentences[i]
            #r = predict.getRightBoundaryMatchedInstances(gold)
            #l = predict.getLeftBoundaryMatchedInstances(gold)
            m = predict.getMissingInstances(gold)
            if m:
                print "----------"
                print gold
                for x in m:
                    print x
                print

    def getNumLeftBoundary(self):
        numLB = 0
        for i, gold in enumerate(self.bgold.sentences):
            predict = self.bpredict.sentences[i]
            l = predict.getLeftBoundaryMatchedInstances(gold)
            numLB += len(l)
        return numLB

    def getGoldStandardDistribution(self, sort_method):
        gs_dist = {}
        for gold in self.bgold.sentences:
            for instance in gold.getInstances():
                tag = instance.tag
                gs_dist[tag] = gs_dist.get(tag, 0) + 1
        results = []
        tags, freqs = zip(*gs_dist.items())
        total = sum(freqs)
        if sort_method == True:
            for (tag, freq) in gs_dist.items():
                results.append((freq, tag, freq/float(total)))
            results.sort()
            results.reverse()
            results = [(tag, freq, pct) for (freq, tag, pct) in results]
            results.append(("TOTAL", total, 1.00))
            return results
        else:    
            for (tag, freq) in gs_dist.items():
                results.append((tag, freq, freq/float(total)))
            results.sort()
            #results = [(tag, freq, pct) for (freq, tag, pct) in results]
            results.append(("TOTAL", total, 1.00))
            return results
        
    def getPredictionDistribution(self, sort_method):
        dist = {}
        for predict in self.bpredict.sentences:
            for instance in predict.getInstances():
                tag = instance.tag
                dist[tag] = dist.get(tag, 0) + 1
        results = []
        tags, freqs = zip(*dist.items())
        total = sum(freqs)
        if sort_method == True:
            for (tag, freq) in dist.items():
                results.append((freq, tag, freq/float(total)))
            results.sort()
            results.reverse()
            results = [(tag, freq, pct) for (freq, tag, pct) in results]
            results.append(("TOTAL", total, 1.00))
            return results
        else:    
            for (tag, freq) in dist.items():
                results.append((tag, freq, freq/float(total)))
            results.sort()
            results.append(("TOTAL", total, 1.00))
            return results

        



    def get_metric(self, boundary):
        metric = {}
        metric['overall'] = {'tp':0, 'answer':0, 'gs':0}
        for (prediction, goldstandard) in zip(self.bpredict.sentences, self.bgold.sentences):
            tps = None
            if boundary == "LEFT":
                tps = prediction.getLeftBoundaryInstances(goldstandard)
            elif boundary == "RIGHT":
                tps = prediction.getRightBoundaryInstances(goldstandard)
            elif boundary == "EITHER":
                tps = prediction.getEitherBoundaryInstances(goldstandard)
            elif boundary == "OVERLAP": 
                tps = prediction.getOverlapInstances(goldstandard)
            else:
                tps = prediction.getExactInstances(goldstandard)
                   
            answers = prediction.getInstances()
            gs = goldstandard.getInstances()
            for instance in tps:
                if not metric.has_key(instance.tag):
                    metric[instance.tag] = {'tp':0, 'answer':0, 'gs':0}
                metric[instance.tag]['tp'] += 1
                metric['overall']['tp']+=1
            for instance in answers:
                if not metric.has_key(instance.tag):
                    metric[instance.tag] = {'tp':0, 'answer':0, 'gs':0}
                metric[instance.tag]['answer'] += 1
                metric['overall']['answer']+=1
            for instance in gs:
                if not metric.has_key(instance.tag):
                    metric[instance.tag] = {'tp':0, 'answer':0, 'gs':0}
                metric[instance.tag]['gs'] += 1
                metric['overall']['gs']+=1
        return metric
    
    def get_performance_metric(self, boundary):
        metric = self.get_metric(boundary)
        performance_metric = {}
        for tag in metric.keys():
            tp = metric[tag]['tp']
            answer = metric[tag]['answer']
            gs = metric[tag]['gs']
            fp = answer - tp
            fn = gs - tp
            (p, r, f) = calc_prf(tp, fp, fn)
            performance_metric[tag] = (tp, fp, fn, gs, p, r, f)
        return performance_metric

    def getCountsByTagName(self, tag_name):
        def filter_by_tags(insts, tag_name):
            finst = []
            for instance in insts:
                if instance.tag == tag_name:
                    finst.append(instance)
            return finst
            
        tps = []
        fps = []
        fns = []
        for (prediction, goldstandard) in zip(self.bpredict.sentences, self.bgold.sentences):
            tps += filter_by_tags(prediction.getCorrectInstances(goldstandard), tag_name)
            fps += filter_by_tags(prediction.getDifferentInstances(goldstandard), tag_name)
            fns += filter_by_tags(goldstandard.getDifferentInstances(prediction), tag_name)
        return tps, fps, fns



    def getCount(tag_name, value_name):
        pass

    def getNumRightBoundary(self):
        numRB = 0
        for i, gold in enumerate(self.bgold.sentences):
            predict = self.bpredict.sentences[i]
            r = predict.getRightBoundaryMatchedInstances(gold)
            numRB += len(r)
        return numRB

    def getNumOverlap(self):
        numOL = 0
        for i, gold in enumerate(self.bgold.sentences):
            predict = self.bpredict.sentences[i]
            o = predict.getOverlapInstances(gold)
            numOL += len(o)
        return numOL
    
    def getNumMissing(self):
        num = 0
        for i, gold in enumerate(self.bgold.sentences):
            predict = self.bpredict.sentences[i]
            m = predict.getMissingInstances(gold)
            num+=len(m)
        return num
        
    def getNumGSMissing(self):
        num = 0
        for i, gold in enumerate(self.bgold.sentences):
            predict = self.bpredict.sentences[i]
            m = predict.getGSMissingInstances(gold)
            num+=len(m)
        return num
        
    def getNumBoundary(self):
        num = 0
        for i, gold in enumerate(self.bgold.sentences):
            predict = self.bpredict.sentences[i]
            m = predict.getBoundaryMatchedInstances(gold)
            num+=len(m)
        return num

def calc_prf(tp, fp, fn):
    try:
        p = float(tp)/(tp+fp)*100
        r = float(tp)/(tp+fn)*100
        f = 2*p*r/(p+r)
    except:
        p = 0.0
        r = 0.0
        f = 0.0
    return (p, r, f)


def show_predict_distribution(filename, sort_method):
    """
    display predicted instance distribution.
    """
    analyser = BIOAnalyser(filename)
    for tag, freq, pct in analyser.getPredictionDistribution(sort_method):
        print "%s\t%2.2f\t%s" % (freq, pct*100, tag)        

def show_goldstandard_distribution(filename, sort_method):
    """
    display goldstandard instance distribution.
    """
    analyser = BIOAnalyser(filename)
    for tag, freq, pct in analyser.getGoldStandardDistribution(sort_method):
        print "%s\t%2.2f\t%s" % (freq, pct*100, tag)        


def stddev(seq):
    m = sum(seq)/float(len(seq))
    b = []
    for x in seq:
        b.append((abs(x-m))**2)
    sda = (sum(b)/float(len(seq)))**0.5
    return sda    

def show_fold_performance_metric(dirname, bio_filename, boundary, avg):
    fold_dirs = [] 
    for fold_dir in os.listdir(dirname):
        if fold_dir[:4] == "fold":
            fold_dirs.append(fold_dir)
    num_fold = len(fold_dirs)

    result_metric = {}

    prfs = {}
    
    for fold_dir in fold_dirs:
        bio_file = os.path.join(dirname, fold_dir, bio_filename)
        analyser = BIOAnalyser(bio_file)
        metric = analyser.get_performance_metric(boundary)
        for tag in metric.keys():
            (tp, fp, fn, n, p, r, f) = metric[tag]
            prfs[tag] = prfs.get(tag, [])
            prfs[tag].append((p,r,f))
            (rtp, rfp, rfn, rn, rp, rr, rf) = result_metric.get(tag, (0, 0, 0, 0, 0.0, 0.0, 0.0))
            result_metric[tag] = (rtp+tp, rfp+fp, rfn+fn, rn+n, rp+p, rr+r, rf+f)
    if avg:
        print "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s" % ("TAGNAME","P", "SD_P", "R", "SD_R", "F", "SD_F","NUM")
    else:
        print "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s" % ("TAGNAME","TP", "FP", "FN", "P", "R", "F", "NUM")
        
    for tag in sorted(result_metric.keys()):
        (tp, fp, fn, n, p, r, f) = result_metric[tag]
        if avg:
            ps, rs, fs = zip(*prfs[tag])
            sdp = stddev(ps)
            sdr = stddev(rs)
            sdf = stddev(fs)
            print "%s\t%2.2f\t%2.2f\t%2.2f\t%2.2f\t%2.2f\t%2.2f\t%d" % (tag,p/num_fold, sdp, r/num_fold, sdr, f/num_fold,sdf,n)
        else:
            (p,r,f) = calc_prf(tp,fp,fn)
            print "%s\t%d\t%d\t%d\t%2.2f\t%2.2f\t%2.2f\t%d" % (tag,tp,fp,fn,p, r, f, n)
            
            
            


def show_performance_metric(filename, boundary):
    analyser = BIOAnalyser(filename)
    metric = analyser.get_performance_metric(boundary)
    
    print "|| '''%s''' || '''%s''' || '''%s''' || '''%s''' || '''%s''' || '''%s''' || '''%s''' || '''%s''' ||" % ("TAG", "TP", "FP", "FN", "P", "R", "F", "N")

    for tag in sorted(metric.keys()):
        if tag != "overall":
            (tp,fp,fn,n,p,r,f) = metric[tag]
            tag = tag.replace('_', ' ')
            #print "%d\t%d\t%d\t%d\t%2.2f\t%2.2f\t%2.2f\t%s" % (tp,fp,fn,n,p,r,f,tag)
            print "|| '''%s''' || %d || %d || %d || %2.2f || %2.2f || %2.2f || %d ||" % (tag,tp,fp,fn,p,r,f,n)
        (tp,fp,fn,n,p,r,f) = metric['overall']
    #print "%d\t%d\t%d\t%d\t%2.2f\t%2.2f\t%2.2f\t%s" % (tp,fp,fn,n,p,r,f,'OVERALL')
    print "|| '''%s''' || %d || %d || %d || %2.2f || %2.2f || %2.2f || %d ||" % ("OVERALL", tp,fp,fn,p,r,f, n)



def main():
    usage = "usage: %prog [options] arg"

    parser = OptionParser(usage)

    parser.add_option("-s", "--sort", 
                      action="store_true", dest="sort_method",
                      help="sort instances by frequency.")
    parser.add_option("-m", "--match", 
                      dest="matchmethod",
                      help="match method, should be either EXACT, RIGTHT, LEFT and EITHER")
    parser.add_option("-f", "--filename", 
                      dest="filename",
                      help="test file name")
    parser.add_option("-a", "--average", 
                      action="store_true", dest="average",
                      help="use average to calcualte the metric in cross-validation.")


    options, args = parser.parse_args()
    
    if len(args)!=2:
        parser.error("incorrect number of argument.")

    filename = args[1]
    action = args[0]
    matchmethod = "EXACT"
    bio_filename = "test.out"
    
    average = options.average
    
    
    if options.matchmethod:
        matchmethod = options.matchmethod
    if options.filename:
        bio_filename = options.filename

    if action == "g":
        show_goldstandard_distribution(filename, options.sort_method)
    elif action == "t":        
        show_predict_distribution(filename, options.sort_method)
    elif action == "p":
        show_performance_metric(filename, matchmethod)
    elif action == "f":
        show_fold_performance_metric(filename, bio_filename, matchmethod, average)
                   
        
if __name__ == "__main__":
    #test()
    main()
    
        
        
