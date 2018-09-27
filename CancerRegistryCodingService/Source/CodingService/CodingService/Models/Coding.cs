using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CodingService.Models
{
    public class CodingService
    {
        public void GetCodes(List<string> histologies, List<string> histologySubtypes, List<string> sites, List<string> RelativeLocation, 
            List<string> behaviors, List<string> grades, List<string> gradeValues, List<string> lateralities, int DxYear,
            ref List<string> histcodes, ref string behaviorcode, ref List<string> sitecodes, ref string gradecode, ref string lateralitycode)
        {
            List<string> histbehaviorcodes = GetHistologyCodes(histologies.ToArray(), behaviors.ToArray(), sites.ToArray());
            DataManager manager = new DataManager();
            //manager.Connect();
            //code behavior if histology code is unambuguous
            if (histbehaviorcodes.Count==1)
            {
                string histbehavior = histbehaviorcodes[0];
                string hist = histbehavior.Split('/')[0];
                string behavior = histbehaviorcodes[0].Split('/')[1];  //initialize with the value from histology search term
                if (behavior.Trim() == "")
                    behaviorcode = "3";  //by default set to 3 if it is blank in histology table
                else
                    behaviorcode = behavior;
                
                //now search in autocodebehavior table
               

                
             
                string nextbehaviorcode = "";

                //code behavior: 3 trumps everything else, otherwise take the highest code
                foreach(string test in behaviors)
                {
                    manager.GetBehaviorCodeByPhrase(test, ref nextbehaviorcode);
                    if (nextbehaviorcode == "3")
                    {
                        behaviorcode = "3";
                        break;
                    }
                        
                    if(nextbehaviorcode.CompareTo(behaviorcode)>0)
                    {
                        behaviorcode = nextbehaviorcode;
                    }
                }


                //code histo-specific sites
                /*
                New histo-specific-site rule - 10/6/2010
                    is the preferred histology melanoma (8720-8790)
                    peripheral nerve (?),
                    or sarcoma (8800-8921)?
                    if it is 
                    get the site code from the histo_specific_site table

               */   
               if(hist.Trim()!="")
                {
                    if ((int.Parse(hist) >= 8720 & int.Parse(hist) <= 8790) | (int.Parse(hist) >= 8800 & int.Parse(hist) <= 8921))
                    {
                        foreach (string test in sites)
                        {
                            string histosite = manager.GetHistospecificSiteCode(int.Parse(hist), test);
                            if (histosite != "")
                                sitecodes.Add(histosite);
                        }

                    }
                }
                
            }

            //handle cytology in eMaRC Plus
            

            sitecodes = GetSiteCodes(sites.ToArray(), RelativeLocation.ToArray());
            /*
                 site exclusion rule 
                 Need to ignore C77* when histo < 9590
            */
            if (histbehaviorcodes.Count == 1)   //unambiguous histology found
            {
                int i = 0;
                string hist = histbehaviorcodes[0].Split('/')[0];
                foreach (string test in sitecodes)
                {
                    if(hist.Trim()!="" && int.Parse(hist)<9590)
                    {
                        if(test.CompareTo("C770")>0 && test.CompareTo("C779")<0)
                        {
                            sitecodes[i] = "";  //set blank
                        }
                    }
                    i++;
                }
                
                //remove all blank
                sitecodes.RemoveAll(x => x.CompareTo("") == 0);
            }
            

            if (histbehaviorcodes.Count==1 & sitecodes.Count==1)  //code grade and laterality only if histology and site are unambiguously determined
            {
                gradecode = GetGradeCode(grades.ToArray(), sitecodes[0], histbehaviorcodes[0].Split('/')[0], DxYear);
                if(!manager.IsPairedOrgan(sitecodes[0]))
                {
                    lateralitycode = "0";
                }
                else if (lateralities.Count==1)
                {
                    manager.GetLateralityCodeByPhrase(lateralities[0], ref lateralitycode);
                }
                
               // lateralitycode = GetLateralityCode(lateralities.ToArray());
            }
            
            //remove behavior
            foreach(string test in histbehaviorcodes)
            {
                if (test.Length >= 4)
                    histcodes.Add(test.Substring(0, 4));
            }
        }

        //public string GetLateralityCode(string[] LateralityPhrases)
        //{
        //    bool found = false;
        //    string code = "";
        //    foreach(string laterality in LateralityPhrases)
        //    {
                
        //        if(laterality.ToUpper().Contains("LEFT"))
        //        {
        //            if (found) code = ""; 
        //            else
        //                code = "2";
        //            found = true;
                    
        //        }
        //        if (laterality.ToUpper().Contains("RIGHT"))
        //        {

        //            if (found) code = "";
        //            else
        //                code = "1";

        //            found = true;
                    
        //        }
        //    }
        //    return code;
        //}
        public List<string> GetHistologyCodes(string[] HistologyPhrases, string[] BehaviorPhrases, string[] SitePhrases)
        {
            DataManager dataManager = new DataManager();
            //dataManager.Connect();
            string hist = string.Empty;
            string behav = string.Empty;
            string behav_save = string.Empty;
            Dictionary<string, string> histologies = new Dictionary<string, string>();
            List<string> preferredlist = new List<string>();

            //get histology and behavior codes from searchterms table
            foreach (string histologyPhrase in HistologyPhrases)
            {
                //search by exact match
                dataManager.GetHistologyBehaviorCodeByPhrase(histologyPhrase, ref hist, ref behav);

                if (hist != "" && !histologies.ContainsKey(hist + "/" + behav))
                    histologies.Add(hist + "/" + behav, histologyPhrase);

                //now search by combining histology and behavior phrase, this is a like search 
                //using both histology and behavior phrase
                //foreach (string behaviorPhrase in BehaviorPhrases)
                //{
                
                //    if(behaviorPhrase!="")
                //    {
                //        List<string> test = dataManager.GetHistologyBehaviorCodeByPhrase(histologyPhrase, behaviorPhrase);
                //        foreach(string temp in test)
                //        {
                //            if (!histologies.ContainsKey(temp))
                //                histologies.Add(temp, histologyPhrase + "|" + behaviorPhrase);

                //        }
                        
                //    }
                        
                //}
                

            }

            //
            

                //if (BehaviorPhrases.Count() > 0)
                //{
                //    //get histology and behavior code by histology phrase and behavior phrase
                //    foreach (string behaviorPhrase in BehaviorPhrases)
                //    {
                //        behav = "";
                //        if(behaviorPhrase=="")
                //        {

                //            //exact search on histology
                //            dataManager.GetHistologyBehaviorCodeByPhrase(histologyPhrase, ref hist, ref behav);
                //        }
                //        else
                //        {
                //            //first see if only histology phrase will get the behavior code
                //            dataManager.GetHistologyBehaviorCodeByPhrase(histologyPhrase, ref hist, ref behav);
                //            //if no behavior code then try both histology and behavior phrase using the like search
                //            dataManager.GetHistologyBehaviorCodeByPhrase(histologyPhrase, behaviorPhrase, ref hist, ref behav);
                //        }
                        
                //        if (hist != "" && !histologies.ContainsKey(hist + "/" + behav))
                //            histologies.Add(hist + "/" + behav, histologyPhrase);
                        
                //        //get the highest behavior code
                //        if (behav != "" && behav_save != "" && int.Parse(behav) > int.Parse(behav_save))
                //            behav_save = behav;
                //        else if (behav_save == "" && behav != "")
                //            behav_save = behav;
                    //}
            //    }
            //    else
            //    {
            //        //find histology code and behavior code by just histology phrase
            //        dataManager.GetHistologyBehaviorCodeByPhrase(histologyPhrase, ref hist, ref behav);
            //        if (behav != "" && behav_save != "" && int.Parse(behav) > int.Parse(behav_save))
            //            behav_save = behav;
            //        else if (behav_save == "" && behav != "")
            //            behav_save = behav;

            //        if (hist != "" && !histologies.ContainsKey(hist + "/" + behav))
            //            histologies.Add(hist + "/" + behav, histologyPhrase);
            //    }
            //}

            //nothing found
            if(histologies.Count==0)
            {
                preferredlist.Add("/");
            }

            else if (histologies.Count == 1)   //just one found
            {
                hist = histologies.Keys.FirstOrDefault();
                preferredlist.Add(hist + behav_save);
            }                
            else  
            {


                //multiple codes => get preferred codes

                //get sortedlist of distinct histologies
                List<string> sortedlist = histologies.Keys.ToList().Distinct().ToList();
                
                sortedlist.Sort();

                //find the highest behavior
                int highbehavior = -1;
                foreach(string test in sortedlist)
                {
                    string b = test.Split('/')[1];
                    if(b!="")
                    {
                        if (int.Parse(b) > highbehavior)
                            highbehavior = int.Parse(b);
                    }
                }

                //do we have a brain site?
                bool brainsite = false;
                foreach(string test in SitePhrases)
                {
                    if (dataManager.BrainSiteExists(test))
                    {
                        brainsite = true;
                        break;
                    }                        
                }

                if(brainsite)
                {
                    //use bb_histo_pairs table which has behavior included as 5th digit
                    for (int i = 0; i< sortedlist.Count; i++)
                    {
                        sortedlist[i] = sortedlist[i].Replace("/", "");
                        
                    }

                   preferredlist = GetPreferredHistologies(sortedlist, "bb_histo_pairs");
                    for (int i = 0; i< preferredlist.Count;i++)
                    {
                        preferredlist[i]= preferredlist[i].Substring(0, 4) + "/" + preferredlist[i].Substring(4, 1);
                    }

                   
                }

                else
                {
                    //remove the behavior code before finding preferred code - general pairs table does not include behavior
                    for (int i = 0; i < sortedlist.Count; i++)
                    {
                        sortedlist[i] = sortedlist[i].Substring(0, 4);
                    }

                    preferredlist = GetPreferredHistologies(sortedlist, "general_histo_pairs");
                    for (int i = 0; i < preferredlist.Count; i++)
                    {
                        if(highbehavior>=0)
                            preferredlist[i] = preferredlist[i].Substring(0, 4) + "/" + highbehavior;
                        else
                            preferredlist[i] = preferredlist[i].Substring(0, 4) + "/";
                    }
                }

            }

            return preferredlist;
        }

        public List<string> GetPreferredHistologies(List<string> histologies, string pairstable)
        {
            if (histologies.Count == 1)
                return histologies;

            DataManager dataManager = new DataManager();
            //dataManager.Connect();

            for (int i = 0; i<histologies.Count-1;i++)
            {

                string hist1 = histologies[i];
                if(hist1.Length>4)
                    hist1 = hist1.Substring(0, 4);

                for (int j = i+1; j<histologies.Count;j++)
                {
                    string hist2 = histologies[j];
                    if (hist2.Length > 4)
                        hist2 = hist2.Substring(0, 4);

                    string preferred = dataManager.GetPreferredHistology(hist1, hist2, pairstable);
                    if(preferred!="")
                    {
                        if (preferred == hist1)
                            histologies[j] = preferred;
                        else
                            histologies[i] = preferred;
                    }
                   

                }
            }

            return histologies.Distinct().ToList();
        }

      
        public List<string> GetSiteCodes(string[] SitePhrases, string[] RelativeLocationPhrases)
        {
            DataManager dataManager = new DataManager();
            //dataManager.Connect();
            Dictionary<string, string> sitecodes = new Dictionary<string, string>();
            string sitecode = string.Empty;
            List<string> preferredlist = new List<string>();
            string site = "";
            foreach (string sitePhrase in SitePhrases)
            {
                site = "";
                dataManager.GetSiteCodeByPhrase(sitePhrase, ref site);
                if (!sitecodes.ContainsKey(site) && site != "")
                {
                    sitecodes.Add(site, sitePhrase);
                }

                
                
            }

           
            //foreach (string locationPhrase in RelativeLocationPhrases)
            //{
            //    site = "";
            //    dataManager.GetSiteCodeByPhrase(locationPhrase, ref site);
            //    if (!sitecodes.ContainsKey(site) && site != "")
            //    {
            //        sitecodes.Add(site, locationPhrase);
            //    }
            //}
            

            preferredlist = sitecodes.Keys.ToList();
            preferredlist.Sort();
           if(preferredlist.Count>1)
            {
                preferredlist =  GetPreferredSites(preferredlist, "sitepairs");
                preferredlist = preferredlist.Distinct().ToList();
               
            }
           
            
            return preferredlist;


        }

        public List<string> GetPreferredSites(List<string> sites, string pairstable)
        {
            if (sites.Count == 1)
                return sites;

            DataManager dataManager = new DataManager();
            //dataManager.Connect();

            for (int i = 0; i < sites.Count - 1; i++)
            {
                string site1 = sites[i];
                for (int j = i + 1; j < sites.Count; j++)
                {
                    string site2 = sites[j];
                    string preferred = dataManager.GetPreferredSite(site1, site2, pairstable).ToString();
                    if (preferred != "")
                    {
                        if (preferred == site1)
                            sites[j] = preferred;
                        else
                            sites[i] = preferred;
                    }


                }
            }

            return sites.Distinct().ToList();
        }
        public string GeBehaviorCode(string BehaviorPhrase)
        {
            DataManager dataManager = new DataManager();
            //dataManager.Connect();
            string behavior = string.Empty;

            dataManager.GetBehaviorCodeByPhrase(BehaviorPhrase, ref behavior);
            return behavior;
        }

        public string GetGradeCode(string[] phrases, string site, string histology, int dxyear)
        {

            DataManager dataManager = new DataManager();
            //dataManager.Connect();
            string grade = string.Empty;

            int gscoregrade = 0;
            int maxgscore = 0;
            int patternscore = 0;
            int maxpatternscore = 0;
            int maxGrade = 0;

            foreach (string phrase in phrases)
            {

                dataManager.GetGradeCodeByPhrase(phrase, site, histology, dxyear, ref grade);  //handles site-specific and histology specific grades

                if (site == "C619")
                {
                    if (phrase.ToLower().Contains("gleason score"))
                    {

                        gscoregrade = int.Parse(grade);
                        if (gscoregrade > maxgscore) maxgscore = gscoregrade;

                    }
                    else if (phrase.ToLower().Contains("gleason pattern"))
                    {
                        dataManager.GetGradeCodeByPhrase(phrase, site, histology, dxyear, ref grade);
                        patternscore = int.Parse(grade);
                        if (patternscore > maxpatternscore) maxpatternscore = patternscore;
                    }
                }

                //if not prostate or gleason score and gleason pattern not found

                if (grade != "")
                {
                    if (int.Parse(grade) > maxGrade)
                        maxGrade = int.Parse(grade);
                }

            }

            if (maxgscore > 0)
                return maxgscore.ToString();
            else if (maxpatternscore > 0)
                return maxpatternscore.ToString();
            else if (maxGrade > 0)
                return maxGrade.ToString();
            else
                return "";
        }


    }
}