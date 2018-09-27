namespace TestHarness
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.txtHistologies = new System.Windows.Forms.TextBox();
            this.txtBehaviors = new System.Windows.Forms.TextBox();
            this.txtLateralities = new System.Windows.Forms.TextBox();
            this.txtGrades = new System.Windows.Forms.TextBox();
            this.txtSites = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.txtCodedOutput = new System.Windows.Forms.TextBox();
            this.btnPost = new System.Windows.Forms.Button();
            this.txtRelativeLocation = new System.Windows.Forms.TextBox();
            this.label6 = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // txtHistologies
            // 
            this.txtHistologies.Location = new System.Drawing.Point(124, 13);
            this.txtHistologies.Name = "txtHistologies";
            this.txtHistologies.Size = new System.Drawing.Size(517, 20);
            this.txtHistologies.TabIndex = 0;
            // 
            // txtBehaviors
            // 
            this.txtBehaviors.Location = new System.Drawing.Point(124, 39);
            this.txtBehaviors.Name = "txtBehaviors";
            this.txtBehaviors.Size = new System.Drawing.Size(517, 20);
            this.txtBehaviors.TabIndex = 1;
            // 
            // txtLateralities
            // 
            this.txtLateralities.Location = new System.Drawing.Point(124, 91);
            this.txtLateralities.Name = "txtLateralities";
            this.txtLateralities.Size = new System.Drawing.Size(517, 20);
            this.txtLateralities.TabIndex = 2;
            // 
            // txtGrades
            // 
            this.txtGrades.Location = new System.Drawing.Point(124, 117);
            this.txtGrades.Name = "txtGrades";
            this.txtGrades.Size = new System.Drawing.Size(517, 20);
            this.txtGrades.TabIndex = 3;
            // 
            // txtSites
            // 
            this.txtSites.Location = new System.Drawing.Point(124, 65);
            this.txtSites.Name = "txtSites";
            this.txtSites.Size = new System.Drawing.Size(517, 20);
            this.txtSites.TabIndex = 4;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(27, 19);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(91, 13);
            this.label1.TabIndex = 5;
            this.label1.Text = "Histology Phrases";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(27, 42);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(90, 13);
            this.label2.TabIndex = 6;
            this.label2.Text = "Behavior Phrases";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(26, 68);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(66, 13);
            this.label3.TabIndex = 7;
            this.label3.Text = "Site Phrases";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(26, 94);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(90, 13);
            this.label4.TabIndex = 8;
            this.label4.Text = "Laterality Phrases";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(25, 124);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(77, 13);
            this.label5.TabIndex = 9;
            this.label5.Text = "Grade Phrases";
            // 
            // txtCodedOutput
            // 
            this.txtCodedOutput.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.txtCodedOutput.Location = new System.Drawing.Point(30, 240);
            this.txtCodedOutput.Multiline = true;
            this.txtCodedOutput.Name = "txtCodedOutput";
            this.txtCodedOutput.Size = new System.Drawing.Size(611, 169);
            this.txtCodedOutput.TabIndex = 10;
            // 
            // btnPost
            // 
            this.btnPost.Location = new System.Drawing.Point(27, 195);
            this.btnPost.Name = "btnPost";
            this.btnPost.Size = new System.Drawing.Size(75, 23);
            this.btnPost.TabIndex = 11;
            this.btnPost.Text = "Get Codes";
            this.btnPost.UseVisualStyleBackColor = true;
            this.btnPost.Click += new System.EventHandler(this.btnPost_Click);
            // 
            // txtRelativeLocation
            // 
            this.txtRelativeLocation.Location = new System.Drawing.Point(124, 144);
            this.txtRelativeLocation.Name = "txtRelativeLocation";
            this.txtRelativeLocation.Size = new System.Drawing.Size(517, 20);
            this.txtRelativeLocation.TabIndex = 12;
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(30, 150);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(90, 13);
            this.label6.TabIndex = 13;
            this.label6.Text = "Relative Location";
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(699, 432);
            this.Controls.Add(this.label6);
            this.Controls.Add(this.txtRelativeLocation);
            this.Controls.Add(this.btnPost);
            this.Controls.Add(this.txtCodedOutput);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.txtSites);
            this.Controls.Add(this.txtGrades);
            this.Controls.Add(this.txtLateralities);
            this.Controls.Add(this.txtBehaviors);
            this.Controls.Add(this.txtHistologies);
            this.Name = "Form1";
            this.Text = "Form1";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox txtHistologies;
        private System.Windows.Forms.TextBox txtBehaviors;
        private System.Windows.Forms.TextBox txtLateralities;
        private System.Windows.Forms.TextBox txtGrades;
        private System.Windows.Forms.TextBox txtSites;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.TextBox txtCodedOutput;
        private System.Windows.Forms.Button btnPost;
        private System.Windows.Forms.TextBox txtRelativeLocation;
        private System.Windows.Forms.Label label6;
    }
}

