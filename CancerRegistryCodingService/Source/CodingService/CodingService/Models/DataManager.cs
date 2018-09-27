using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Data.SqlClient;
using System.Configuration;
using System.Data;
namespace CodingService.Models
{
    class DataManager
    {
        SqlConnection con = new SqlConnection();
        public DataManager()
        {
            string connectionString = ConfigurationManager.ConnectionStrings["eMaRCPlus"].ConnectionString;
            con.ConnectionString = connectionString;
        }
        //public void Connect()
        //{
        //    string connectionString = ConfigurationManager.ConnectionStrings["eMaRCPlus"].ConnectionString;
        //    con.ConnectionString = connectionString;
        //    try
        //    {
        //        if (con.State == ConnectionState.Closed)
        //            con.Open();
        //    }
        //    catch (Exception ex)
        //    {
        //        throw ex;
        //    }
        //}

       
        //public void ImportAnnotatedResult(string result, string fileName)
        //{
        //    try
        //    {

        //        SqlCommand cmd = new SqlCommand();
        //        cmd.CommandText = "insert into NLPResult (TaggedOutput, TransactionId) values (@TaggedOutput, @TransactionId)";
        //        cmd.Connection = con;
        //        con.Open();
        //        cmd.Parameters.AddWithValue("TaggedOutput", result);
        //        cmd.Parameters.AddWithValue("TransactionId", fileName);
        //        cmd.ExecuteNonQuery();
        //        cmd.Dispose();
        //        con.Close();
        //    }
        //    catch (Exception ex)
        //    {
                
        //        throw ex;
        //    }
        //    finally
        //    {
        //        if (con.State == ConnectionState.Open)
        //            con.Close();
        //    }

        //}

        public bool BrainSiteExists(string siteterm)
        {

            bool retval = false;
            try
            {
                SqlCommand cmd = new SqlCommand();
                cmd.CommandText = "select count(*) from brain_terms where searchterm = @searchterm";
                cmd.Connection = con;
                cmd.Parameters.AddWithValue("searchterm", siteterm);

                if (con.State == ConnectionState.Closed)
                     con.Open();

                    if (int.Parse(cmd.ExecuteScalar().ToString()) > 0)
                {
                    retval= true;
                }
                else
                {
                    retval = false;
                }

            }
            catch (Exception ex)
            {
             
                throw ex;
            }
            finally
            {
                if (con.State == ConnectionState.Open)
                    con.Close();
            }
            return retval;
        }

        //public bool FileExists(string fileName)
        //{
        //    try
        //    {
        //        SqlCommand cmd = new SqlCommand();
        //        cmd.CommandText = "select count(*) from NLPResult where TransactionId = @TransactionId";
        //        cmd.Connection = con;
        //        cmd.Parameters.AddWithValue("TransactionId", fileName);
        //        if (con.State == ConnectionState.Closed)
        //            con.Open();
        //        if (int.Parse(cmd.ExecuteScalar().ToString()) > 0)
        //        {
        //            return true;
        //        }
        //        else
        //        {
        //            return false;
        //        }

        //    }
        //    catch (Exception ex)
        //    {
               
        //        throw ex;
        //    }
        //    finally
        //    {
        //        if (con.State == ConnectionState.Open)
        //            con.Close();
        //    }
        //}

        public string GetPreferredHistology(string hist1, string hist2, string pairstable)
        {
            //check parameters
            if (hist1 == "" || hist2 == "")
                return "";
            

            if(hist1.CompareTo(hist2)>0)
            {
                throw new Exception("Incorrect parameters - hist1 must be less than hist2. hist1 = " + hist1 + ", hist2 = " + hist2);
            }
            
            try
            {
                SqlCommand cmd = new SqlCommand();
                cmd.CommandText = "select histo_pairs_preferred from " + pairstable + " where histo_pairs_low = @hist1 and histo_pairs_high = @hist2";
                
                cmd.Connection = con;
                cmd.Parameters.AddWithValue("hist1", hist1);
                cmd.Parameters.AddWithValue("hist2", hist2);

                cmd.Connection = con;

                if (con.State == ConnectionState.Closed)
                    con.Open();

                var retval = cmd.ExecuteScalar();
                if (retval != null)
                    return retval.ToString();
                else
                    return "";

            }
            catch (Exception ex)
            {
                throw ex;
            }
            finally
            {
                if (con.State == ConnectionState.Open)
                    con.Close();
            }
        }

        //public string GeBehaviorCode(string term)
        //{
        //    try
        //    {
        //        SqlCommand cmd = new SqlCommand();
        //        cmd.Connection = con;
        //        cmd.CommandText = "select code from behaviorautocode where searchterm = @term";
        //        cmd.Connection = con;
        //        cmd.Parameters.AddWithValue("term", term);
        //        var retval = cmd.ExecuteScalar();
        //        if (retval == null)
        //            return "";
        //        else
        //            return retval.ToString();

        //    }
        //    catch (Exception ex)
        //    {
        //        throw ex;
        //    }
        //}
        public void GetLateralityCodeByPhrase(string term, ref string code)
        {
            try
            {
                SqlCommand cmd = new SqlCommand();
                cmd.Connection = con;
                cmd.CommandText = "select code from lateralautocode where searchterm = @term";
                cmd.Connection = con;
                if (con.State == ConnectionState.Closed)
                    con.Open();
                cmd.Parameters.AddWithValue("term", term);
                var retval = cmd.ExecuteScalar();
                if (retval == null)
                    code= "";
                else
                    code= retval.ToString();

            }
            catch (Exception ex)
            {
                throw ex;
            }
            finally
            {
                if (con.State == ConnectionState.Open)
                    con.Close();
            }

        }

        public string GetHistospecificSiteCode(int HistologyCode, string term)
        {
            string columnname = "";
            if(HistologyCode>= 8720 && HistologyCode<=8790)
            {
                columnname = "melanoma_sitecode";
            }
            else if(HistologyCode>=8800 && HistologyCode<=8921)
            {
                columnname = "sarcoma_sitecode";
            }

            try
            {
                SqlCommand cmd = new SqlCommand();
                cmd.Connection = con;
                cmd.CommandText = "select " + columnname + " from HISTO_SPECIFIC_SITE where sitetext = @term";
                cmd.Connection = con;
                cmd.Parameters.AddWithValue("term", term);

                if (con.State == ConnectionState.Closed)
                    con.Open();

                var retval = cmd.ExecuteScalar();
                if (retval == null)
                    return  "";
                else
                    return retval.ToString();

            }

            catch (Exception ex)
            {
                throw ex;
            }
            finally
            {
                if (con.State == ConnectionState.Open)
                    con.Close();
            }
        }
        public string GetPreferredSite(string site1, string site2, string pairstable)
        {
            //check parameters
            if (site1.Trim() == "" || site2.Trim() == "")
                return "";

            string highcode = "";
            string lowcode = "";

            if (site1.CompareTo(site2)>0)
            {
                //throw new Exception("Incorrect parameters - site1 must be less than site2. site1 = " + site1 + ", site2 = " + site2);
                highcode = site1;
                lowcode = site2;
            }
            else
            {
                highcode = site2;
                lowcode = site1;
            }

            try
            {
                SqlCommand cmd = new SqlCommand();
                cmd.CommandText = "select site_pair_preferred from " + pairstable + " where site_pair_low = @site1 and site_pair_high = @site2";

                cmd.Connection = con;

                if (con.State == ConnectionState.Closed)
                    con.Open();

                cmd.Parameters.AddWithValue("site1", lowcode);
                cmd.Parameters.AddWithValue("site2", highcode);

                cmd.Connection = con;
                var retval = cmd.ExecuteScalar();
                if (retval != null)
                    return retval.ToString();
                else
                    return "";

            }
            catch (Exception ex)
            {
                throw ex;
            }
            finally
            {
                if (con.State == ConnectionState.Open)
                    con.Close();
            }
        }

        //public string GetTaggedOutputByMsgId(int MsgId)
        //{
        //    string result = string.Empty;
        //    try
        //    {
        //        SqlCommand cmd = new SqlCommand();
        //        cmd.CommandText = "select TaggedOutput from NLPResult where msgid = " + MsgId;
        //        cmd.Connection = con;
        //        result = cmd.ExecuteScalar().ToString();

        //    }
        //    catch (Exception ex)
        //    {

        //    }
        //    finally
        //    {
        //        if (con.State == ConnectionState.Open)
        //            con.Close();
        //    }
        //    return result;
        //}

        //public string GetTaggedOutputByFileNmae(string fileName)
        //{
        //    string result = string.Empty;
        //    try
        //    {
        //        SqlCommand cmd = new SqlCommand();
        //        cmd.CommandText = "select TaggedOutput from NLPResult where TransactionId = @TransactionId";
        //        cmd.Connection = con;
        //        cmd.Parameters.AddWithValue("TransactionId", fileName);
        //        result = cmd.ExecuteScalar().ToString();

        //    }
        //    catch (Exception ex)
        //    {

        //    }
        //    finally
        //    {
        //        if (con.State == ConnectionState.Open)
        //            con.Close();
        //    }
        //    return result;
        //}

        public void GetHistologyBehaviorCodeByPhrase(string HistologyPhrase, ref string histology, ref string behavior)
        {

            try
            {
                SqlCommand cmd = new SqlCommand();
                cmd.Connection = con;
                cmd.CommandText = "select histology_code, behavior from search_terms_combined where searchterm = @term";
                cmd.Connection = con;
                cmd.Parameters.AddWithValue("term", HistologyPhrase);
                SqlDataAdapter ad = new SqlDataAdapter();
                ad.SelectCommand = cmd;
                DataTable dt = new DataTable();

                if (con.State == ConnectionState.Closed)
                    con.Open();

                ad.Fill(dt);
                if (dt.Rows.Count > 0)
                {
                    histology = dt.Rows[0]["histology_code"].ToString();
                    behavior = dt.Rows[0]["behavior"].ToString();
                }

            }
            catch (Exception ex)
            {
                throw ex;
            }
            finally
            {
                if (con.State == ConnectionState.Open)
                    con.Close();
            }
        }

        public List<string> GetHistologyBehaviorCodeByPhrase(string HistologyPhrase, string BehaviorPhrase)
        {

            try
            {
                List<string> hists = new List<string>();
                SqlCommand cmd = new SqlCommand();
                cmd.Connection = con;
                cmd.CommandText = "select histology_code, behavior from search_terms_combined where searchterm like '%' + @HistologyTerm + '%' and searchterm like '%' + @BehaviorTerm + '%'";
                cmd.Connection = con;
                cmd.Parameters.AddWithValue("HistologyTerm", HistologyPhrase);
                cmd.Parameters.AddWithValue("BehaviorTerm", BehaviorPhrase);
                SqlDataAdapter ad = new SqlDataAdapter();
                ad.SelectCommand = cmd;
                DataTable dt = new DataTable();

                if (con.State == ConnectionState.Closed)
                    con.Open();

                ad.Fill(dt);
                foreach(DataRow dr in dt.Rows)
                {
                    hists.Add(dr["histology_code"].ToString() + "/" + dr["behavior"].ToString());
                }


                return hists;
            }
            catch (Exception ex)
            {
                throw ex;
            }
            finally
            {
                if (con.State == ConnectionState.Open)
                    con.Close();
            }
        }

        public bool IsPairedOrgan(string sitecode)
        {
            try
            {
                SqlCommand cmd = new SqlCommand();

                //first try with site and relative location
                cmd.CommandText = "select count(*) from paired_organ_sites where site = '" + sitecode + "'";
                cmd.Connection = con;
                

                if (con.State == ConnectionState.Closed)
                    con.Open();
                if(int.Parse(cmd.ExecuteScalar().ToString())>0)
                {
                    return true;
                }
                else
                {
                    return false;
                }


            }
            catch(Exception ex)
            {
                throw ex;
            }
            finally
            {
                con.Close();
            }
                
        }

        public void GetSiteCodeByPhrase(string sitephrase, string relativelocationphrase, ref string site)
        {

            try
            {
                SqlCommand cmd = new SqlCommand();

                //first try with site and relative location
                cmd.CommandText = "select sitecode from search_terms_sites where searchterm like '%' + @siteterm + '%' and searchterm like '%' + @relativelocationterm + '%'";
                cmd.Connection = con;
                cmd.Parameters.AddWithValue("siteterm", sitephrase);
                cmd.Parameters.AddWithValue("relativelocationterm", relativelocationphrase);

                if (con.State == ConnectionState.Closed)
                    con.Open();

                SqlDataAdapter ad = new SqlDataAdapter();
                ad.SelectCommand = cmd;
                DataTable dt = new DataTable();
                ad.Fill(dt);
                if (dt.Rows.Count > 0)
                {
                    site = dt.Rows[0]["sitecode"].ToString();

                }


            }
            catch (Exception ex)
            {
                throw ex;
            }
            finally
            {
                if (con.State == ConnectionState.Open)
                    con.Close();
            }
        }

        public void GetSiteCodeByPhrase(string sitephrase, ref string site)
        {

            try
            {
                SqlCommand cmd = new SqlCommand();
                DataTable dt;
                SqlDataAdapter ad = new SqlDataAdapter();
                cmd.CommandText = "select sitecode from search_terms_sites where searchterm = @siteterm";
                cmd.Connection = con;
                cmd.Parameters.AddWithValue("siteterm", sitephrase);
                dt = new DataTable();
                ad = new SqlDataAdapter();

                if (con.State == ConnectionState.Closed)
                    con.Open();

                ad.SelectCommand = cmd;
                ad.Fill(dt);
                if (dt.Rows.Count > 0)
                {
                    site = dt.Rows[0]["sitecode"].ToString();

                }


            }
            catch (Exception ex)
            {
                throw ex;
            }
            finally
            {
                if (con.State == ConnectionState.Open)
                    con.Close();
            }
        }
        public void GetBehaviorCodeByPhrase(string phrase, ref string behavior)
        {

            try
            {
                SqlCommand cmd = new SqlCommand();
                cmd.CommandText = "select code from behaviorautocode where searchterm = @term";
                cmd.Connection = con;
                cmd.Parameters.AddWithValue("term", phrase);
                SqlDataAdapter ad = new SqlDataAdapter();
                ad.SelectCommand = cmd;
                DataTable dt = new DataTable();

                if (con.State == ConnectionState.Closed)
                    con.Open();

                ad.Fill(dt);
                if (dt.Rows.Count > 0)
                {
                    behavior = dt.Rows[0]["code"].ToString();

                }

            }
            catch (Exception ex)
            {
                throw ex;
            }
            finally
            {
                if (con.State == ConnectionState.Open)
                    con.Close();
            }
        }


        public void GetGradeCodeByPhrase(string phrase, string site, string histology, int dxyear, ref string grade)
        {
            //site exception is also handled here
            try
            {
                string code_exception = "";
                string siterange = "";
                int maxCode = 0;
                int maxGleasonScore = 0;
                int maxGScore = 0;
                int maxPScore = 0;

                int dx_year_effective = 0;
                if (dxyear > 2014) dx_year_effective = 2014;
                else dx_year_effective = 2013;


                SqlCommand cmd = new SqlCommand();

                cmd.CommandText = "select code, code_exception, primary_site from autocode_grade where searchterm = @term";
                cmd.Connection = con;
                cmd.Parameters.AddWithValue("term", phrase);
                //cmd.Parameters.AddWithValue("dxyear", dx_year_effective);
                SqlDataAdapter ad = new SqlDataAdapter();
                ad.SelectCommand = cmd;

                if (con.State == ConnectionState.Closed)
                    con.Open();

                DataTable dt = new DataTable();
                ad.Fill(dt);
                if (dt.Rows.Count > 0)
                {
                    grade = dt.Rows[0]["code"].ToString();
                    siterange = dt.Rows[0]["primary_site"].ToString();
                    code_exception = dt.Rows[0]["code_exception"] == System.DBNull.Value ? "" : dt.Rows[0].ToString();

                    //if site is in siterange return code_exception otherwise return code
                    if (code_exception != "")
                    {
                        if (InRangeOrList(site, siterange))
                        {
                            grade = code_exception;
                        }
                    }

                    //grade based on histology - ignores phrase altogether
                    cmd = new SqlCommand("select grade_code from autocode_grade_exception where histology_code = @histology");
                    cmd.Parameters.AddWithValue("histology", histology);
                    cmd.Connection = con;
                    ad = new SqlDataAdapter();
                    ad.SelectCommand = cmd;
                    dt = new DataTable();
                    ad.Fill(dt);
                    if (dt.Rows.Count > 0)
                    {
                        grade = dt.Rows[0]["grade_code"].ToString();
                    }

                }

            }
            catch (Exception ex)
            {
                throw ex;
            }
            finally
            {
                if (con.State == ConnectionState.Open)
                    con.Close();
            }
        }

        public bool InRangeOrList(string value, string RangeOrList)
        {
            string[] temp = RangeOrList.Split(',');
            foreach (string test in temp)
            {
                if (test.Contains("-"))
                {
                    string high = test.Split('-')[0];
                    string low = test.Split('-')[1];
                    if (high.CompareTo(value) > 0 && low.CompareTo(value) < 0)
                    {
                        return true;
                    }
                }
                else
                {
                    if (test.CompareTo(value) == 0)
                    {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}