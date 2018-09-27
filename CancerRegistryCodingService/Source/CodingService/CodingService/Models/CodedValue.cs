using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CodingService.Models
{
    public class CodedValue
    {
        public List<string> HistologCodes { get; set; }
        public string BehaviorCode { get; set; }
        public List<string> SiteCodes { get; set; }
        public string LateralityCode { get; set; }
        public string GradeCode { get; set; }
        
    }
}