const jwt = require('jsonwebtoken');

const authenticate = async (req, res, next) => {
  let token = req.cookies?.token;

  if (!token && req.headers.authorization) {
    if (req.headers.authorization.startsWith('Bearer ')) {
      token = req.headers.authorization.split(' ')[1];
    }
  }

  if (!token) {
    return res.status(401).json({ message: 'No token provided' });
  }

  try {
    const decoded = jwt.verify(token, process.env.JWT);
    
    // Check if token is about to expire (within 5 minutes)
    const currentTime = Math.floor(Date.now() / 1000);
    const tokenExp = decoded.exp;
    const timeUntilExpiry = tokenExp - currentTime;
    
    // If token expires in less than 5 minutes (300 seconds), refresh it
    if (timeUntilExpiry < 300 && timeUntilExpiry > 0) {
      const refreshToken = req.cookies?.refreshToken || req.headers['x-refresh-token'];
      
      if (refreshToken) {
        try {
          const refreshSecret = process.env.JWT_REFRESH || process.env.JWT + '_refresh';
          const refreshDecoded = jwt.verify(refreshToken, refreshSecret);
          
          // Generate new access token
          const newToken = jwt.sign(
            { id: refreshDecoded.id, email: refreshDecoded.email },
            process.env.JWT,
            { expiresIn: '1h' }
          );
          
          // Set new token in response header for client to update
          res.setHeader('X-New-Token', newToken);
          
          // Also update the cookie if possible
          res.cookie("token", newToken, {
            httpOnly: true,
            sameSite: "None",
            secure: true,
            maxAge: 3600000
          });
          
          // Update decoded with new token data
          req.user = jwt.verify(newToken, process.env.JWT);
          return next();
        } catch (refreshErr) {
          // Refresh token invalid, proceed with current token anyway
          // It will fail shortly anyway
        }
      }
    }

    req.user = decoded;
    next();
  } catch (err) {
    // Token expired - try to refresh using refresh token
    const refreshToken = req.cookies?.refreshToken || req.headers['x-refresh-token'];
    
    if (refreshToken) {
      try {
        const refreshSecret = process.env.JWT_REFRESH || process.env.JWT + '_refresh';
        const refreshDecoded = jwt.verify(refreshToken, refreshSecret);
        
        // Generate new access token
        const newToken = jwt.sign(
          { id: refreshDecoded.id, email: refreshDecoded.email },
          process.env.JWT,
          { expiresIn: '1h' }
        );
        
        // Set new token in response header
        res.setHeader('X-New-Token', newToken);
        
        res.cookie("token", newToken, {
          httpOnly: true,
          sameSite: "None",
          secure: true,
          maxAge: 3600000
        });
        
        req.user = jwt.verify(newToken, process.env.JWT);
        next();
      } catch (refreshErr) {
        return res.status(403).json({ message: 'Invalid or expired token' });
      }
    } else {
      return res.status(403).json({ message: 'Invalid or expired token' });
    }
  }
};

module.exports = authenticate;
