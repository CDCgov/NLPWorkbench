using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using CodingService.Models;
using System.Net.Http.Formatting;
using System.Web.Http.Cors;

namespace CodingService.Controllers
{
    [EnableCors(origins: "*", headers: "*", methods: "*")]
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
            List<string> histologies = new List<string>();
            string behavior = "";
            string laterality = "";            
            string grade = "";
            List<string> sites = new List<string>();
            try
            {
                new CodingService.Models.CodingService().GetCodes(input.HistologyPhrases, input.HistologySubtypePhrases, input.SitePhrases,
                input.RelativeLocationPhrases, input.BehaviorPhrases, input.GradePhrases, input.GradeValuePhrases,
                input.LateralityPhrases, input.DiagnosisDate,
                ref histologies, ref behavior, ref sites, ref grade, ref laterality
                );

                CodedValue codedValue = new CodedValue();
                codedValue.HistologCodes = histologies;
                codedValue.SiteCodes = sites;
                codedValue.GradeCode = grade;
                codedValue.BehaviorCode = behavior;
                codedValue.LateralityCode = laterality;
                return Content(HttpStatusCode.Created, codedValue, new JsonMediaTypeFormatter());
            }
            catch(Exception ex)
            {
                return Content(HttpStatusCode.InternalServerError, "Error: " + ex.ToString(), new JsonMediaTypeFormatter());
            }
            

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
