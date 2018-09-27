using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using CodingService.Models;
using System.Net.Http.Formatting;

namespace CodingService.Controllers
{
    public class CancerRegistryCodingController : ApiController
    {

        
        // GET: api/CancerRegistryCoding
        //public IEnumerable<string> Get()
        //{
        //    return new string[] { "value1", "value2" };
        //}

        //// GET: api/CancerRegistryCoding/5
        //public string Get(int id)
        //{
        //    return "value";
        //}

        // POST: api/CancerRegistryCoding
        //public void Post([FromBody]string value)
        //{
        //}
        public IHttpActionResult Post (CodingInput input)
        {
            string histologybehavior = "";
            string laterality = "";            
            string grade = "";
            string site = "";

            new CodingService.Models.CodingService().GetCodes(input.HistologyPhrases, input.HistologySubtypePhrases, input.SitePhrases,
                input.RelativeLocationPhrases, input.BehaviorPhrases, input.GradePhrases, input.GradeValuePhrases, 
                input.LateralityPhrases, input.DiagnosisDate,
                ref histologybehavior, ref site, ref grade, ref laterality
                );

            CodedValue codedValue = new CodedValue();
            codedValue.HistologyBehaviorCode = histologybehavior;
            codedValue.SiteCode = site;
            codedValue.GradeCode = grade;
            codedValue.LateralityCode = laterality;
            return Content(HttpStatusCode.Created, codedValue, new JsonMediaTypeFormatter());

        }
        //public void Post([FromBody]string value)
        //{
        //}
        // PUT: api/CancerRegistryCoding/5
        //public void Put(int id, [FromBody]string value)
        //{
        //}

        //// DELETE: api/CancerRegistryCoding/5
        //public void Delete(int id)
        //{
        //}
    }
}
