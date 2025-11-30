const functions = require("firebase-functions");
const { Resend } = require("resend");

const RESEND_API_KEY = process.env.RESEND_API_KEY;

exports.sendProviderInvite = functions.https.onRequest(async (req, res) => {
  res.set("Access-Control-Allow-Origin", "*");
  res.set("Access-Control-Allow-Headers", "Content-Type");

  if (req.method === "OPTIONS") {
    return res.status(200).end();
  }

  try {
    const { email, inviteCode } = req.body;

    if (!RESEND_API_KEY) {
      console.error("Missing RESEND_API_KEY");
      return res.status(500).json({ error: "server_config_error" });
    }
//ToDo: Add Provider Verification Logic
    const resend = new Resend(RESEND_API_KEY);

    await resend.emails.send({
      from: "SMART AIR <noreply@smartair20.win>",
      to: email,
      subject: "SMART AIR Invitation Code",
      html: `<p>Hello,</p>
               <p>You have received an invitation code from a parent to access SMART AIR as a Provider.</p>
               <p>Your invitation code is:</p>
               <h2>${inviteCode}</h2>
               <p>This code is valid for <b>7 days</b> and can be used only once.</p>
               <p>Please open the SMART AIR app, log in with your provider account, and enter this code in the <em>Accept Invitation</em> section to bind with the corresponding child.</p>
               <p>Thanks,<br/>SMART AIR Team</p>`,
    });

    return res.status(200).json({ success: true });
  } catch (err) {
    console.error(err);
    return res.status(500).json({ error: "send_error" });
  }
});

