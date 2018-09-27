using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Web.Script.Serialization;
using System.Web;

namespace TestHarness
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void btnPost_Click(object sender, EventArgs e)
        {

//            tubulovillous adenoma
//adenocarcinoma
//cancinoma


//intramucusal
            var baseAddress = "http://localhost:42595/Api/CancerRegistryCoding";
            //var baseAddress = "http://clew.phiresearchlab.org/CancerRegistryCodingService/Api/CancerRegistryCoding";
            var http = (HttpWebRequest)WebRequest.Create(new Uri(baseAddress));
            http.Accept = "application/json";
            http.ContentType = "application/json";
            http.Method = "POST";
            CodeInput input = new CodeInput();
            input.DiagnosisDate = 2018;
            input.HistologyPhrases = new List<string>( txtHistologies.Text.Split(';'));
            input.BehaviorPhrases = new List<string>(txtBehaviors.Text.Split(';'));
            input.SitePhrases = new List<string>(txtSites.Text.Split(';'));
            input.LateralityPhrases = new List<string>(txtLateralities.Text.Split(';'));
            input.GradePhrases = new List<string>(txtGrades.Text.Split(';'));
            input.RelativeLocationPhrases = new List<string>(txtRelativeLocation.Text.Split(';'));


            var json = new JavaScriptSerializer().Serialize(input);

            string parsedContent = json; 
            //parsedContent= {"DiagnosisDate":2018,"HistologyPhrases":["adenocarcinoma","carcinoma"],"HistologySubtypePhrases":["tubulovillous adenoma"],"RelativeLocationPhrases":["Random","Sigmoid"],"SitePhrases":["Colon"],"LateralityPhrases":[],"BehaviorPhrases":["intramucosal"],"GradePhrases":[],"GradeValuePhrases":[]}";
            ASCIIEncoding encoding = new ASCIIEncoding();
            Byte[] bytes = encoding.GetBytes(parsedContent);

            Stream newStream = http.GetRequestStream();
            newStream.Write(bytes, 0, bytes.Length);
            newStream.Close();
            WebResponse response = null;
            try
            {
                response = http.GetResponse();
                
                var stream = response.GetResponseStream();
                var sr = new StreamReader(stream);
                var content = sr.ReadToEnd();
                txtCodedOutput.Text = content;
            }
            catch(WebException ex)
            {
                MessageBox.Show(ex.ToString());
            }
            catch(HttpException ex)
            {
                MessageBox.Show(ex.ToString());
            }
            catch(Exception ex)
            {
                MessageBox.Show(ex.ToString());
            }
           
        }
    }
}
