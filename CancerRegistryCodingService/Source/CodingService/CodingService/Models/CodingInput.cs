using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CodingService.Models
{
    public class CodingInput
    {
        public int DiagnosisDate { get; set; }

        public List<string> HistologyPhrases { get; set; }
        public List<string> HistologySubtypePhrases { get; set; }
        public List<string> RelativeLocationPhrases { get; set; }
        public List<string> SitePhrases { get; set; }
        public List<string> LateralityPhrases { get; set; }
        public List<string> BehaviorPhrases { get; set; }
        public List<string> GradePhrases { get; set; }
        public List<string> GradeValuePhrases { get; set; }
    }

    
}